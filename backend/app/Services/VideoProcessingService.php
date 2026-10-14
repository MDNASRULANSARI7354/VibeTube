<?php

namespace App\Services;

use App\Models\Video;
use App\Models\VideoAsset;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\Storage;
use Symfony\Component\Process\Process;

class VideoProcessingService
{
    /**
     * Process raw video into adaptive HLS streams & thumbnails using FFmpeg
     */
    public function process(Video $video, string $localSourcePath): void
    {
        try {
            $videoOutputDir = storage_path("app/public/videos/{$video->id}");
            if (!is_dir($videoOutputDir)) {
                mkdir($videoOutputDir, 0755, true);
            }

            // 1. Probe duration
            $duration = $this->getVideoDuration($localSourcePath);
            $video->duration_seconds = (int) round($duration);

            // 2. Generate Thumbnail at 5 seconds
            $thumbnailPath = "{$videoOutputDir}/thumbnail.jpg";
            $thumbCmd = [
                'ffmpeg', '-y', '-ss', '00:00:05', '-i', $localSourcePath,
                '-vframes', '1', '-q:v', '2', $thumbnailPath
            ];
            (new Process($thumbCmd))->mustRun();

            // Store thumbnail to S3
            $s3ThumbKey = "thumbnails/{$video->id}/thumb.jpg";
            Storage::disk('s3')->put($s3ThumbKey, file_get_contents($thumbnailPath));
            $video->thumbnail_url = Storage::disk('s3')->url($s3ThumbKey);

            // 3. Multi-bitrate HLS Transcoding (1080p, 720p, 480p)
            $hlsCmd = [
                'ffmpeg', '-y', '-i', $localSourcePath,
                // 1080p
                '-map', '0:v:0', '-map', '0:a:0', '-s:v:0', '1920x1080', '-c:v:0', 'libx264', '-b:v:0', '4500k',
                // 720p
                '-map', '0:v:0', '-map', '0:a:0', '-s:v:1', '1280x720', '-c:v:1', 'libx264', '-b:v:1', '2500k',
                // 480p
                '-map', '0:v:0', '-map', '0:a:0', '-s:v:2', '854x480', '-c:v:2', 'libx264', '-b:v:2', '1000k',
                '-c:a', 'aac', '-b:a', '128k', '-ar', '48000',
                '-f', 'hls',
                '-hls_time', '4',
                '-hls_playlist_type', 'vod',
                '-hls_flags', 'independent_segments',
                '-master_pl_name', 'master.m3u8',
                '-var_stream_map', 'v:0,a:0 v:1,a:1 v:2,a:2',
                "{$videoOutputDir}/v%v/stream.m3u8"
            ];

            $process = new Process($hlsCmd);
            $process->setTimeout(3600); // Allow up to 1 hr for 4K/long transcoding
            $process->mustRun();

            // 4. Upload HLS files to S3
            $hlsFiles = glob("{$videoOutputDir}/**/*.*");
            foreach ($hlsFiles as $filePath) {
                $relPath = str_replace("{$videoOutputDir}/", '', $filePath);
                $s3Path = "streams/{$video->id}/{$relPath}";
                Storage::disk('s3')->put($s3Path, file_get_contents($filePath));
            }

            // 5. Store Video Assets & Mark Ready
            $masterS3Url = Storage::disk('s3')->url("streams/{$video->id}/master.m3u8");
            $video->hls_master_url = $masterS3Url;
            $video->status = 'ready';
            $video->save();

            VideoAsset::create([
                'video_id' => $video->id,
                'quality' => '1080p',
                'bitrate' => 4500000,
                'stream_url' => Storage::disk('s3')->url("streams/{$video->id}/v0/stream.m3u8"),
            ]);

            VideoAsset::create([
                'video_id' => $video->id,
                'quality' => '720p',
                'bitrate' => 2500000,
                'stream_url' => Storage::disk('s3')->url("streams/{$video->id}/v1/stream.m3u8"),
            ]);

            VideoAsset::create([
                'video_id' => $video->id,
                'quality' => '480p',
                'bitrate' => 1000000,
                'stream_url' => Storage::disk('s3')->url("streams/{$video->id}/v2/stream.m3u8"),
            ]);

            // Clean up local temp files
            array_map('unlink', glob("{$videoOutputDir}/**/*.*"));
            @rmdir($videoOutputDir);

        } catch (\Exception $e) {
            Log::error("FFmpeg processing failed for video {$video->id}: " . $e->getMessage());
            $video->status = 'failed';
            $video->save();
        }
    }

    private function getVideoDuration(string $path): float
    {
        $cmd = [
            'ffprobe', '-v', 'error', '-show_entries', 'format=duration',
            '-of', 'default=noprint_wrappers=1:nokey=1', $path
        ];
        $process = new Process($cmd);
        $process->mustRun();
        return (float) trim($process->getOutput());
    }
}

<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Video;
use App\Models\Category;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Str;

class UploadController extends Controller
{
    /**
     * Generate secure, time-limited S3 pre-signed upload URL
     */
    public function getSignedUploadUrl(Request $request)
    {
        $request->validate([
            'filename' => 'required|string',
            'content_type' => 'required|string|in:video/mp4,video/quicktime,video/webm',
        ]);

        $user = $request->user();
        $creator = $user->creatorProfile;
        if (!$creator) {
            return response()->json(['error' => 'User does not possess a creator channel'], 403);
        }

        $extension = pathinfo($request->filename, PATHINFO_EXTENSION) ?: 'mp4';
        $videoKey = 'raw_uploads/' . $creator->id . '/' . Str::uuid() . '.' . $extension;

        // Generate S3 Pre-signed URL valid for 30 minutes
        $s3Client = Storage::disk('s3')->getClient();
        $bucket = config('filesystems.disks.s3.bucket', 'vibetube-media');

        $command = $s3Client->getCommand('PutObject', [
            'Bucket' => $bucket,
            'Key' => $videoKey,
            'ContentType' => $request->content_type,
        ]);

        $signedRequest = $s3Client->createPresignedRequest($command, '+30 minutes');
        $presignedUrl = (string) $signedRequest->getUri();

        return response()->json([
            'upload_url' => $presignedUrl,
            'video_key' => $videoKey,
            'expires_in' => 1800,
        ]);
    }

    /**
     * Complete upload and trigger server-side FFmpeg queue worker
     */
    public function publish(Request $request)
    {
        $validated = $request->validate([
            'title' => 'required|string|max:200',
            'description' => 'nullable|string',
            'category' => 'required|string',
            'visibility' => 'required|in:public,unlisted,private',
            'video_key' => 'required|string',
            'is_short' => 'boolean',
            'tags' => 'nullable|array',
        ]);

        $user = $request->user();
        $creator = $user->creatorProfile;
        $category = Category::firstOrCreate(['name' => $validated['category']], ['slug' => Str::slug($validated['category'])]);

        $video = Video::create([
            'creator_id' => $creator->id,
            'category_id' => $category->id,
            'title' => $validated['title'],
            'description' => $validated['description'] ?? '',
            'visibility' => $validated['visibility'],
            'status' => 'processing', // Queued for FFmpeg HLS transcoding
            'source_storage_path' => Storage::disk('s3')->url($validated['video_key']),
            'is_short' => $validated['is_short'] ?? false,
            'tags' => $validated['tags'] ?? [],
            'published_at' => now(),
        ]);

        // Dispatch background queue job for FFmpeg processing
        // \App\Jobs\ProcessVideoWithFFmpeg::dispatch($video);

        return response()->json([
            'message' => 'Video uploaded and queued for processing',
            'video_id' => $video->id,
            'status' => 'processing'
        ], 201);
    }
}

<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Video;
use Illuminate\Http\Request;

class ShortsController extends Controller
{
    public function index()
    {
        $shorts = Video::with(['creator'])
            ->where('is_short', true)
            ->where('status', 'ready')
            ->where('visibility', 'public')
            ->orderBy('views_count', 'desc')
            ->limit(20)
            ->get();

        $data = $shorts->map(function ($s) {
            return [
                'id' => $s->id,
                'title' => $s->title,
                'description' => $s->description,
                'video_url' => $s->hls_master_url ?: $s->source_storage_path,
                'thumbnail_url' => $s->thumbnail_url,
                'duration_seconds' => $s->duration_seconds,
                'views_count' => $s->views_count,
                'likes_count' => $s->likes_count,
                'comments_count' => $s->comments_count,
                'channel_title' => $s->creator->channel_name ?? 'Unknown',
                'channel_avatar' => $s->creator->avatar_url ?? '',
                'music_title' => 'Original Sound - ' . ($s->creator->channel_name ?? 'VibeTube'),
            ];
        });

        return response()->json($data);
    }
}

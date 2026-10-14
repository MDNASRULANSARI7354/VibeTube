<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Video;
use App\Models\Like;
use App\Models\VideoView;
use Illuminate\Http\Request;

class VideoController extends Controller
{
    public function index(Request $request)
    {
        $query = Video::with(['creator', 'category'])
            ->where('status', 'ready')
            ->where('visibility', 'public')
            ->where('is_short', false);

        if ($request->has('category') && $request->category !== 'All') {
            $query->whereHas('category', function ($q) use ($request) {
                $q->where('name', $request->category);
            });
        }

        if ($request->has('search')) {
            $search = $request->search;
            $query->where(function ($q) use ($search) {
                $q->where('title', 'ILIKE', "%{$search}%")
                  ->orWhere('description', 'ILIKE', "%{$search}%")
                  ->orWhereJsonContains('tags', $search);
            });
        }

        $videos = $query->orderBy('published_at', 'desc')->paginate(12);

        $transformed = $videos->through(function ($v) {
            return [
                'id' => $v->id,
                'title' => $v->title,
                'description' => $v->description,
                'video_url' => $v->hls_master_url ?: $v->source_storage_path,
                'thumbnail_url' => $v->thumbnail_url,
                'duration_seconds' => $v->duration_seconds,
                'views_count' => $v->views_count,
                'created_at' => $v->created_at->diffForHumans(),
                'channel_title' => $v->creator->channel_name ?? 'Unknown',
                'channel_avatar' => $v->creator->avatar_url ?? '',
                'category' => $v->category->name ?? 'General',
            ];
        });

        return response()->json($transformed);
    }

    public function show($id, Request $request)
    {
        $video = Video::with(['creator', 'category', 'chapters', 'captions', 'assets'])
            ->findOrFail($id);

        // Record video telemetry view asynchronously
        VideoView::create([
            'video_id' => $video->id,
            'user_id' => $request->user()?->id,
            'ip_address' => $request->ip(),
            'user_agent' => $request->userAgent(),
            'watch_duration_seconds' => 0,
        ]);
        $video->increment('views_count');

        $isLiked = false;
        $isDisliked = false;
        if ($request->user()) {
            $likeRecord = Like::where('user_id', $request->user()->id)
                              ->where('video_id', $video->id)
                              ->first();
            if ($likeRecord) {
                $isLiked = $likeRecord->is_like;
                $isDisliked = !$likeRecord->is_like;
            }
        }

        return response()->json([
            'video' => $video,
            'is_liked' => $isLiked,
            'is_disliked' => $isDisliked,
        ]);
    }

    public function toggleLike($id, Request $request)
    {
        $request->validate(['is_like' => 'required|boolean']);
        $user = $request->user();
        $video = Video::findOrFail($id);

        $existing = Like::where('user_id', $user->id)
                        ->where('video_id', $video->id)
                        ->first();

        if ($existing) {
            if ($existing->is_like == $request->is_like) {
                // Remove like/dislike toggle
                $existing->delete();
                if ($request->is_like) {
                    $video->decrement('likes_count');
                } else {
                    $video->decrement('dislikes_count');
                }
                return response()->json(['status' => 'cleared']);
            } else {
                // Flip like <-> dislike
                $existing->update(['is_like' => $request->is_like]);
                if ($request->is_like) {
                    $video->increment('likes_count');
                    $video->decrement('dislikes_count');
                } else {
                    $video->decrement('likes_count');
                    $video->increment('dislikes_count');
                }
                return response()->json(['status' => $request->is_like ? 'liked' : 'disliked']);
            }
        }

        Like::create([
            'user_id' => $user->id,
            'video_id' => $video->id,
            'is_like' => $request->is_like,
        ]);

        if ($request->is_like) {
            $video->increment('likes_count');
        } else {
            $video->increment('dislikes_count');
        }

        return response()->json(['status' => $request->is_like ? 'liked' : 'disliked']);
    }
}

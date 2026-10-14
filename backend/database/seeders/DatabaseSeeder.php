<?php

namespace Database\Seeders;

use App\Models\Category;
use App\Models\CreatorProfile;
use App\Models\User;
use App\Models\Video;
use App\Models\VideoChapter;
use App\Models\Comment;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Str;

class DatabaseSeeder extends Seeder
{
    public function run(): void
    {
        // 1. Seed Categories
        $categories = [
            ['name' => 'Tech & Coding', 'slug' => 'tech-coding'],
            ['name' => 'Music', 'slug' => 'music'],
            ['name' => 'Gaming', 'slug' => 'gaming'],
            ['name' => 'Design', 'slug' => 'design'],
            ['name' => 'Podcasts', 'slug' => 'podcasts'],
            ['name' => 'AI Trends', 'slug' => 'ai-trends'],
        ];

        $categoryModels = [];
        foreach ($categories as $cat) {
            $categoryModels[$cat['name']] = Category::create($cat);
        }

        // 2. Seed Creators & Users
        $creatorUser1 = User::create([
            'name' => 'TechPulse Studio',
            'email' => 'contact@techpulse.io',
            'password' => Hash::make('Secret123!'),
            'handle' => '@techpulse',
            'avatar_url' => 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=200&auto=format&fit=crop&q=80',
            'role' => 'creator',
        ]);

        $channel1 = CreatorProfile::create([
            'user_id' => $creatorUser1->id,
            'channel_name' => 'TechPulse Studio',
            'handle' => '@techpulse',
            'bio' => 'Exploring the edge of mobile architecture, Jetpack Compose, and cloud distributed video streaming systems.',
            'banner_url' => 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=1200&auto=format&fit=crop&q=80',
            'avatar_url' => $creatorUser1->avatar_url,
            'is_verified' => true,
            'subscriber_count' => 1420000,
            'total_views' => 15800000,
            'is_monetized' => true,
        ]);

        // 3. Seed Videos
        $video1 = Video::create([
            'creator_id' => $channel1->id,
            'category_id' => $categoryModels['Tech & Coding']->id,
            'title' => 'Building a Distributed Video Streaming Pipeline with Laravel & FFmpeg',
            'description' => 'In this deep dive, we architect a complete YouTube-scale video ingestion and HLS transcoding pipeline using Laravel workers, PostgreSQL, and Android Media3 ExoPlayer playback.',
            'visibility' => 'public',
            'status' => 'ready',
            'is_short' => false,
            'duration_seconds' => 596,
            'source_storage_path' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
            'hls_master_url' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
            'thumbnail_url' => 'https://images.unsplash.com/photo-1574717024653-61fd2cf4d44d?w=800&auto=format&fit=crop&q=80',
            'views_count' => 384500,
            'likes_count' => 28400,
            'dislikes_count' => 120,
            'comments_count' => 642,
            'tags' => ['Laravel', 'Android', 'FFmpeg', 'Streaming', 'Compose'],
            'ai_summary' => "Key takeaways:\n• Multi-bitrate HLS segmentation achieves adaptive bitrate switching.\n• Offloading FFmpeg tasks to background worker queues prevents blocking web threads.\n• Signed S3 pre-signed upload URLs maintain strict credential confidentiality on mobile clients.",
            'transcript' => "0:00 Intro\n1:20 Client signed upload flow\n3:45 Background FFmpeg queue\n6:10 Adaptive HLS playlist",
            'published_at' => now()->subDays(2),
        ]);

        VideoChapter::create(['video_id' => $video1->id, 'title' => 'Introduction', 'start_seconds' => 0]);
        VideoChapter::create(['video_id' => $video1->id, 'title' => 'Client Signed Upload Flow', 'start_seconds' => 80]);
        VideoChapter::create(['video_id' => $video1->id, 'title' => 'FFmpeg Queue', 'start_seconds' => 225]);
        VideoChapter::create(['video_id' => $video1->id, 'title' => 'Adaptive HLS', 'start_seconds' => 370]);

        // Seed Short
        Video::create([
            'creator_id' => $channel1->id,
            'category_id' => $categoryModels['Tech & Coding']->id,
            'title' => '3 Android Compose tricks that feel like magic! 🔥 #Shorts #AndroidDev',
            'description' => 'Quick tips for UI state hoisting and modifier chaining.',
            'visibility' => 'public',
            'status' => 'ready',
            'is_short' => true,
            'duration_seconds' => 45,
            'source_storage_path' => 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4',
            'thumbnail_url' => 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&auto=format&fit=crop&q=80',
            'views_count' => 89400,
            'likes_count' => 12400,
            'comments_count' => 340,
            'tags' => ['Shorts', 'AndroidDev'],
            'published_at' => now()->subHours(8),
        ]);
    }
}

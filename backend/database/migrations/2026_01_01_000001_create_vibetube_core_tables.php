<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        // 1. Users Table
        Schema::create('users', function (Blueprint $table) {
            $table->uuid('id')->primary();
            $table->string('name');
            $table->string('email')->unique();
            $table->timestamp('email_verified_at')->nullable();
            $table->string('password');
            $table->string('handle')->unique();
            $table->string('avatar_url')->nullable();
            $table->enum('role', ['user', 'creator', 'admin'])->default('user');
            $table->rememberToken();
            $table->timestamps();
        });

        // 2. Creator Profiles Table
        Schema::create('creator_profiles', function (Blueprint $table) {
            $table->uuid('id')->primary();
            $table->uuid('user_id')->constrained('users')->onDelete('cascade');
            $table->string('channel_name');
            $table->string('handle')->unique();
            $table->text('bio')->nullable();
            $table->string('banner_url')->nullable();
            $table->string('avatar_url')->nullable();
            $table->boolean('is_verified')->default(false);
            $table->unsignedBigInteger('subscriber_count')->default(0);
            $table->unsignedBigInteger('total_views')->default(0);
            $table->boolean('is_monetized')->default(false);
            $table->timestamps();
        });

        // 3. Categories Table
        Schema::create('categories', function (Blueprint $table) {
            $table->id();
            $table->string('name')->unique();
            $table->string('slug')->unique();
            $table->string('icon')->nullable();
            $table->timestamps();
        });

        // 4. Videos Table
        Schema::create('videos', function (Blueprint $table) {
            $table->uuid('id')->primary();
            $table->uuid('creator_id')->constrained('creator_profiles')->onDelete('cascade');
            $table->foreignId('category_id')->nullable()->constrained('categories')->onDelete('set null');
            $table->string('title', 255);
            $table->text('description')->nullable();
            $table->enum('visibility', ['public', 'unlisted', 'private'])->default('public');
            $table->enum('status', ['uploading', 'processing', 'ready', 'failed'])->default('uploading');
            $table->boolean('is_short')->default(false);
            $table->unsignedInteger('duration_seconds')->default(0);
            $table->string('source_storage_path')->nullable();
            $table->string('hls_master_url')->nullable();
            $table->string('thumbnail_url')->nullable();
            $table->unsignedBigInteger('views_count')->default(0);
            $table->unsignedBigInteger('likes_count')->default(0);
            $table->unsignedBigInteger('dislikes_count')->default(0);
            $table->unsignedBigInteger('comments_count')->default(0);
            $table->jsonb('tags')->nullable();
            $table->text('ai_summary')->nullable();
            $table->longText('transcript')->nullable();
            $table->timestamp('published_at')->nullable()->index();
            $table->timestamps();
            $table->softDeletes();
        });

        // 5. Video Assets (Adaptive HLS Renditions 1080p, 720p, 480p, 360p)
        Schema::create('video_assets', function (Blueprint $table) {
            $table->uuid('id')->primary();
            $table->uuid('video_id')->constrained('videos')->onDelete('cascade');
            $table->string('quality'); // 1080p, 720p, 480p, 360p
            $table->unsignedInteger('bitrate'); // in bps
            $table->string('stream_url');
            $table->unsignedBigInteger('file_size_bytes')->default(0);
            $table->timestamps();
        });

        // 6. Subscriptions Table
        Schema::create('subscriptions', function (Blueprint $table) {
            $table->id();
            $table->uuid('subscriber_id')->constrained('users')->onDelete('cascade');
            $table->uuid('creator_id')->constrained('creator_profiles')->onDelete('cascade');
            $table->boolean('notifications_enabled')->default(true);
            $table->timestamps();
            $table->unique(['subscriber_id', 'creator_id']);
        });

        // 7. Likes Table
        Schema::create('likes', function (Blueprint $table) {
            $table->id();
            $table->uuid('user_id')->constrained('users')->onDelete('cascade');
            $table->uuid('video_id')->constrained('videos')->onDelete('cascade');
            $table->boolean('is_like')->default(true); // true = like, false = dislike
            $table->timestamps();
            $table->unique(['user_id', 'video_id']);
        });

        // 8. Comments Table
        Schema::create('comments', function (Blueprint $table) {
            $table->uuid('id')->primary();
            $table->uuid('video_id')->constrained('videos')->onDelete('cascade');
            $table->uuid('user_id')->constrained('users')->onDelete('cascade');
            $table->uuid('parent_comment_id')->nullable()->constrained('comments')->onDelete('cascade');
            $table->text('content');
            $table->unsignedInteger('likes_count')->default(0);
            $table->boolean('is_pinned')->default(false);
            $table->timestamps();
            $table->softDeletes();
        });

        // 9. Playlists Table
        Schema::create('playlists', function (Blueprint $table) {
            $table->uuid('id')->primary();
            $table->uuid('user_id')->constrained('users')->onDelete('cascade');
            $table->string('title');
            $table->text('description')->nullable();
            $table->enum('visibility', ['public', 'unlisted', 'private'])->default('public');
            $table->timestamps();
        });

        // 10. Playlist Videos Pivot Table
        Schema::create('playlist_videos', function (Blueprint $table) {
            $table->id();
            $table->uuid('playlist_id')->constrained('playlists')->onDelete('cascade');
            $table->uuid('video_id')->constrained('videos')->onDelete('cascade');
            $table->unsignedInteger('position')->default(0);
            $table->timestamps();
            $table->unique(['playlist_id', 'video_id']);
        });

        // 11. Watch History Table
        Schema::create('watch_history', function (Blueprint $table) {
            $table->id();
            $table->uuid('user_id')->constrained('users')->onDelete('cascade');
            $table->uuid('video_id')->constrained('videos')->onDelete('cascade');
            $table->unsignedInteger('progress_seconds')->default(0);
            $table->boolean('is_completed')->default(false);
            $table->timestamp('watched_at')->useCurrent();
            $table->timestamps();
            $table->index(['user_id', 'watched_at']);
        });

        // 12. Notifications Table
        Schema::create('notifications', function (Blueprint $table) {
            $table->uuid('id')->primary();
            $table->uuid('user_id')->constrained('users')->onDelete('cascade');
            $table->string('type');
            $table->string('title');
            $table->text('message');
            $table->jsonb('data')->nullable();
            $table->timestamp('read_at')->nullable();
            $table->timestamps();
        });

        // 13. Video Views (Telemetry & Deduplication)
        Schema::create('video_views', function (Blueprint $table) {
            $table->id();
            $table->uuid('video_id')->constrained('videos')->onDelete('cascade');
            $table->uuid('user_id')->nullable()->constrained('users')->onDelete('set null');
            $table->string('ip_address', 45)->nullable();
            $table->string('user_agent')->nullable();
            $table->unsignedInteger('watch_duration_seconds')->default(0);
            $table->timestamps();
            $table->index(['video_id', 'created_at']);
        });

        // 14. Reports Table
        Schema::create('reports', function (Blueprint $table) {
            $table->id();
            $table->uuid('user_id')->constrained('users')->onDelete('cascade');
            $table->uuid('video_id')->nullable()->constrained('videos')->onDelete('cascade');
            $table->uuid('comment_id')->nullable()->constrained('comments')->onDelete('cascade');
            $table->string('reason');
            $table->text('details')->nullable();
            $table->enum('status', ['pending', 'reviewed', 'dismissed', 'actioned'])->default('pending');
            $table->timestamps();
        });

        // 15. Captions Table
        Schema::create('captions', function (Blueprint $table) {
            $table->id();
            $table->uuid('video_id')->constrained('videos')->onDelete('cascade');
            $table->string('language_code', 10);
            $table->string('label', 50);
            $table->string('vtt_storage_path');
            $table->boolean('is_auto_generated')->default(false);
            $table->timestamps();
        });

        // 16. Video Chapters Table
        Schema::create('video_chapters', function (Blueprint $table) {
            $table->id();
            $table->uuid('video_id')->constrained('videos')->onDelete('cascade');
            $table->string('title');
            $table->unsignedInteger('start_seconds');
            $table->timestamps();
            $table->index(['video_id', 'start_seconds']);
        });

        // 17. Livestreams Table
        Schema::create('livestreams', function (Blueprint $table) {
            $table->uuid('id')->primary();
            $table->uuid('creator_id')->constrained('creator_profiles')->onDelete('cascade');
            $table->string('title');
            $table->text('description')->nullable();
            $table->string('stream_key')->unique();
            $table->string('playback_url')->nullable();
            $table->enum('status', ['scheduled', 'live', 'ended'])->default('scheduled');
            $table->unsignedInteger('viewer_count')->default(0);
            $table->timestamp('scheduled_at')->nullable();
            $table->timestamp('started_at')->nullable();
            $table->timestamp('ended_at')->nullable();
            $table->timestamps();
        });

        // 18. Livestream Messages Table
        Schema::create('livestream_messages', function (Blueprint $table) {
            $table->id();
            $table->uuid('livestream_id')->constrained('livestreams')->onDelete('cascade');
            $table->uuid('user_id')->constrained('users')->onDelete('cascade');
            $table->string('message', 500);
            $table->boolean('is_super_chat')->default(false);
            $table->decimal('super_chat_amount', 8, 2)->nullable();
            $table->timestamps();
            $table->index(['livestream_id', 'created_at']);
        });

        // 19. Analytics Events Table
        Schema::create('analytics_events', function (Blueprint $table) {
            $table->id();
            $table->uuid('video_id')->nullable()->constrained('videos')->onDelete('cascade');
            $table->uuid('creator_id')->nullable()->constrained('creator_profiles')->onDelete('cascade');
            $table->string('event_type'); // impression, play, 25%, 50%, 75%, 100%, click
            $table->jsonb('properties')->nullable();
            $table->timestamp('occurred_at')->useCurrent();
            $table->index(['video_id', 'event_type', 'occurred_at']);
        });

        // 20. Creator Earnings Table
        Schema::create('creator_earnings', function (Blueprint $table) {
            $table->id();
            $table->uuid('creator_id')->constrained('creator_profiles')->onDelete('cascade');
            $table->date('period_date');
            $table->decimal('ad_revenue', 10, 2)->default(0.00);
            $table->decimal('membership_revenue', 10, 2)->default(0.00);
            $table->decimal('super_chat_revenue', 10, 2)->default(0.00);
            $table->decimal('total_revenue', 10, 2)->default(0.00);
            $table->timestamps();
            $table->unique(['creator_id', 'period_date']);
        });

        // 21. Payment Transactions Table
        Schema::create('payment_transactions', function (Blueprint $table) {
            $table->uuid('id')->primary();
            $table->uuid('user_id')->constrained('users')->onDelete('cascade');
            $table->uuid('creator_id')->nullable()->constrained('creator_profiles')->onDelete('set null');
            $table->enum('type', ['channel_membership', 'super_chat', 'creator_payout']);
            $table->decimal('amount', 10, 2);
            $table->string('currency', 3)->default('USD');
            $table->string('payment_provider')->default('stripe');
            $table->string('provider_reference')->unique()->nullable();
            $table->enum('status', ['pending', 'completed', 'failed', 'refunded'])->default('pending');
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('payment_transactions');
        Schema::dropIfExists('creator_earnings');
        Schema::dropIfExists('analytics_events');
        Schema::dropIfExists('livestream_messages');
        Schema::dropIfExists('livestreams');
        Schema::dropIfExists('video_chapters');
        Schema::dropIfExists('captions');
        Schema::dropIfExists('reports');
        Schema::dropIfExists('video_views');
        Schema::dropIfExists('notifications');
        Schema::dropIfExists('watch_history');
        Schema::dropIfExists('playlist_videos');
        Schema::dropIfExists('playlists');
        Schema::dropIfExists('comments');
        Schema::dropIfExists('likes');
        Schema::dropIfExists('subscriptions');
        Schema::dropIfExists('video_assets');
        Schema::dropIfExists('videos');
        Schema::dropIfExists('categories');
        Schema::dropIfExists('creator_profiles');
        Schema::dropIfExists('users');
    }
};

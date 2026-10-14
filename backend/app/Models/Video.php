<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Concerns\HasUuids;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class Video extends Model
{
    use HasFactory, HasUuids, SoftDeletes;

    protected $fillable = [
        'creator_id',
        'category_id',
        'title',
        'description',
        'visibility',
        'status',
        'is_short',
        'duration_seconds',
        'source_storage_path',
        'hls_master_url',
        'thumbnail_url',
        'views_count',
        'likes_count',
        'dislikes_count',
        'comments_count',
        'tags',
        'ai_summary',
        'transcript',
        'published_at',
    ];

    protected $casts = [
        'is_short' => 'boolean',
        'tags' => 'array',
        'published_at' => 'datetime',
    ];

    public function creator()
    {
        return $this->belongsTo(CreatorProfile::class, 'creator_id');
    }

    public function category()
    {
        return $this->belongsTo(Category::class);
    }

    public function assets()
    {
        return $this->hasMany(VideoAsset::class);
    }

    public function comments()
    {
        return $this->hasMany(Comment::class)->whereNull('parent_comment_id');
    }

    public function chapters()
    {
        return $this->hasMany(VideoChapter::class)->orderBy('start_seconds', 'asc');
    }

    public function captions()
    {
        return $this->hasMany(Caption::class);
    }

    public function likes()
    {
        return $this->hasMany(Like::class);
    }

    public function views()
    {
        return $this->hasMany(VideoView::class);
    }
}

<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Concerns\HasUuids;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class Comment extends Model
{
    use HasFactory, HasUuids, SoftDeletes;

    protected $fillable = [
        'video_id',
        'user_id',
        'parent_comment_id',
        'content',
        'likes_count',
        'is_pinned',
    ];

    public function video()
    {
        return $this->belongsTo(Video::class);
    }

    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public function replies()
    {
        return $this->hasMany(Comment::class, 'parent_comment_id');
    }
}

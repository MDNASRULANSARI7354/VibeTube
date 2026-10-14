<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Concerns\HasUuids;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Playlist extends Model
{
    use HasFactory, HasUuids;

    protected $fillable = ['user_id', 'title', 'description', 'visibility'];

    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public function videos()
    {
        return $this->belongsToMany(Video::class, 'playlist_videos')
                    ->withPivot('position')
                    ->orderBy('playlist_videos.position', 'asc');
    }
}

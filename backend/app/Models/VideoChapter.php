<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class VideoChapter extends Model
{
    use HasFactory;

    protected $fillable = ['video_id', 'title', 'start_seconds'];

    public function video()
    {
        return $this->belongsTo(Video::class);
    }
}

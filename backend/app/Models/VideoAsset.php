<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Concerns\HasUuids;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class VideoAsset extends Model
{
    use HasFactory, HasUuids;

    protected $fillable = [
        'video_id',
        'quality',
        'bitrate',
        'stream_url',
        'file_size_bytes',
    ];

    public function video()
    {
        return $this->belongsTo(Video::class);
    }
}

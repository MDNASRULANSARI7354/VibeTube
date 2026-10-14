<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Concerns\HasUuids;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class CreatorProfile extends Model
{
    use HasFactory, HasUuids;

    protected $fillable = [
        'user_id',
        'channel_name',
        'handle',
        'bio',
        'banner_url',
        'avatar_url',
        'is_verified',
        'subscriber_count',
        'total_views',
        'is_monetized',
    ];

    protected $casts = [
        'is_verified' => 'boolean',
        'is_monetized' => 'boolean',
    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public function videos()
    {
        return $this->hasMany(Video::class, 'creator_id');
    }

    public function subscribers()
    {
        return $this->hasMany(Subscription::class, 'creator_id');
    }

    public function earnings()
    {
        return $this->hasMany(CreatorEarning::class, 'creator_id');
    }
}

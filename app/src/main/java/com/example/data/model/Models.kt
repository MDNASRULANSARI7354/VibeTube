package com.example.data.model

enum class VideoVisibility {
    PUBLIC, UNLISTED, PRIVATE
}

data class VideoChapter(
    val title: String,
    val timeSeconds: Long,
    val timeFormatted: String
)

data class Video(
    val id: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val durationSeconds: Long,
    val durationFormatted: String,
    val viewsCount: Long,
    val publishedAt: String,
    val channelId: String,
    val channelTitle: String,
    val channelAvatarUrl: String,
    val channelSubscribers: String,
    val isVerified: Boolean = true,
    var likesCount: Long,
    var dislikesCount: Long = 0,
    var isLiked: Boolean = false,
    var isDisliked: Boolean = false,
    var isSubscribed: Boolean = false,
    val category: String,
    val tags: List<String> = emptyList(),
    val aiSummary: String = "",
    val transcript: String = "",
    val chapters: List<VideoChapter> = emptyList(),
    val commentsCount: Long = 0,
    val visibility: VideoVisibility = VideoVisibility.PUBLIC,
    val isShort: Boolean = false
)

data class ShortItem(
    val id: String,
    val videoUrl: String,
    val title: String,
    val channelTitle: String,
    val channelAvatarUrl: String,
    val musicTitle: String,
    var likesCount: Long,
    var commentsCount: Long,
    var isLiked: Boolean = false,
    var isSubscribed: Boolean = false,
    val tags: List<String> = emptyList()
)

data class Comment(
    val id: String,
    val videoId: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val text: String,
    var likesCount: Long,
    var isLiked: Boolean = false,
    val timeAgo: String,
    val repliesCount: Int = 0
)

data class Channel(
    val id: String,
    val name: String,
    val handle: String,
    val avatarUrl: String,
    val bannerUrl: String,
    val subscriberCount: String,
    val videoCount: Int,
    var isSubscribed: Boolean = false,
    val bio: String,
    val joinedDate: String
)

data class Playlist(
    val id: String,
    val title: String,
    val videoCount: Int,
    val isPrivate: Boolean = false,
    val thumbnailResUrl: String = "",
    val updatedAt: String = "Recently updated",
    val videoIds: MutableList<String> = mutableListOf()
)

data class Category(
    val id: String,
    val name: String
)

data class User(
    val id: String,
    val name: String,
    val email: String,
    val handle: String,
    val avatarUrl: String,
    val token: String? = null
)

data class CreatorAnalytics(
    val totalViews: String,
    val watchTimeHours: String,
    val subscribersGained: String,
    val estimatedEarnings: String,
    val impressionsClickThroughRate: String,
    val topVideos: List<Video>
)

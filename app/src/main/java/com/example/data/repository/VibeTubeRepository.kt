package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class VibeTubeRepository {

    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos: StateFlow<List<Video>> = _videos.asStateFlow()

    private val _shorts = MutableStateFlow<List<ShortItem>>(emptyList())
    val shorts: StateFlow<List<ShortItem>> = _shorts.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    private val _watchHistory = MutableStateFlow<List<Video>>(emptyList())
    val watchHistory: StateFlow<List<Video>> = _watchHistory.asStateFlow()

    private val _commentsMap = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
    val commentsMap: StateFlow<Map<String, List<Comment>>> = _commentsMap.asStateFlow()

    private val _channels = MutableStateFlow<Map<String, Channel>>(emptyMap())
    val channels: StateFlow<Map<String, Channel>> = _channels.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(
        User(
            id = "user_101",
            name = "Alex Vance",
            email = "alex@vibetube.io",
            handle = "@alexvance_dev",
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
            token = "vib_sanctum_sample_token_xyz"
        )
    )
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val initialChannels = listOf(
            Channel(
                id = "ch_techpulse",
                name = "TechPulse Studio",
                handle = "@techpulse",
                avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=200&auto=format&fit=crop&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=1200&auto=format&fit=crop&q=80",
                subscriberCount = "1.42M",
                videoCount = 184,
                isSubscribed = true,
                bio = "Exploring the edge of mobile architecture, Jetpack Compose, and cloud distributed video streaming systems.",
                joinedDate = "Jan 2021"
            ),
            Channel(
                id = "ch_soundwave",
                name = "Vibe Beats & Lo-Fi",
                handle = "@vibebeats",
                avatarUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=200&auto=format&fit=crop&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=1200&auto=format&fit=crop&q=80",
                subscriberCount = "890K",
                videoCount = 92,
                isSubscribed = false,
                bio = "Curated chillhop, synthwave, and coding study beats around the clock.",
                joinedDate = "Aug 2022"
            ),
            Channel(
                id = "ch_devforge",
                name = "FullStack Forge",
                handle = "@devforge",
                avatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=200&auto=format&fit=crop&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=1200&auto=format&fit=crop&q=80",
                subscriberCount = "620K",
                videoCount = 145,
                isSubscribed = true,
                bio = "Building high-performance backends with Laravel, PostgreSQL, Docker, and Android clients.",
                joinedDate = "Mar 2023"
            )
        )
        _channels.value = initialChannels.associateBy { it.id }

        val initialVideos = listOf(
            Video(
                id = "vid_1",
                title = "Building a Distributed Video Streaming Pipeline with Laravel & FFmpeg",
                description = "In this deep dive, we architect a complete YouTube-scale video ingestion and HLS transcoding pipeline using Laravel workers, PostgreSQL, and Android Media3 ExoPlayer playback.",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1574717024653-61fd2cf4d44d?w=800&auto=format&fit=crop&q=80",
                durationSeconds = 596,
                durationFormatted = "9:56",
                viewsCount = 384500,
                publishedAt = "2 days ago",
                channelId = "ch_techpulse",
                channelTitle = "TechPulse Studio",
                channelAvatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=200&auto=format&fit=crop&q=80",
                channelSubscribers = "1.42M",
                likesCount = 28400,
                category = "Tech & Coding",
                tags = listOf("Laravel", "Android", "FFmpeg", "Streaming", "Compose"),
                aiSummary = "Key takeaways:\n• Multi-bitrate HLS segmentation achieves adaptive bitrate switching with minimal buffering.\n• Offloading FFmpeg tasks to background worker queues prevents blocking web threads.\n• Signed S3 pre-signed upload URLs maintain strict credential confidentiality on mobile clients.",
                transcript = "0:00 Introduction to modern video architecture\n1:20 Client side signed upload flow\n3:45 Background FFmpeg worker queue\n6:10 Adaptive bitrate HLS playlist\n8:30 Android ExoPlayer caching strategy",
                chapters = listOf(
                    VideoChapter("Introduction", 0, "0:00"),
                    VideoChapter("Client Upload Flow", 80, "1:20"),
                    VideoChapter("FFmpeg Queue", 225, "3:45"),
                    VideoChapter("Adaptive HLS", 370, "6:10"),
                    VideoChapter("ExoPlayer Integration", 510, "8:30")
                ),
                commentsCount = 642,
                isLiked = true,
                isSubscribed = true
            ),
            Video(
                id = "vid_2",
                title = "Night Coding & Lo-Fi Beats • Deep Focus Ambient Mix [2026]",
                description = "Continuous relaxing beat waves for programming, designing, studying, and late night creative sessions. High fidelity master with stereo imaging.",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
                durationSeconds = 653,
                durationFormatted = "10:53",
                viewsCount = 890200,
                publishedAt = "1 week ago",
                channelId = "ch_soundwave",
                channelTitle = "Vibe Beats & Lo-Fi",
                channelAvatarUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=200&auto=format&fit=crop&q=80",
                channelSubscribers = "890K",
                likesCount = 65000,
                category = "Music",
                tags = listOf("Lo-Fi", "Focus", "Chill", "Coding"),
                aiSummary = "An ambient soundscape engineered for sustained deep work periods, maintaining steady 75-80 BPM rhythms without sudden dynamic spikes.",
                transcript = "Continuous ambient lo-fi soundscape playing throughout the session.",
                chapters = listOf(
                    VideoChapter("Dawn Prelude", 0, "0:00"),
                    VideoChapter("Electric Rain", 180, "3:00"),
                    VideoChapter("Cyber Midnight", 420, "7:00")
                ),
                commentsCount = 1290,
                isLiked = false,
                isSubscribed = false
            ),
            Video(
                id = "vid_3",
                title = "Clean Architecture with Jetpack Compose & StateFlow: Zero AI Slop Guide",
                description = "Ditch messy single-activity spaghetti code. Learn how to construct reactive UIs with M3 design tokens, unidirectional data flow, and decoupled repositories.",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=800&auto=format&fit=crop&q=80",
                durationSeconds = 240,
                durationFormatted = "4:00",
                viewsCount = 142100,
                publishedAt = "3 days ago",
                channelId = "ch_devforge",
                channelTitle = "FullStack Forge",
                channelAvatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=200&auto=format&fit=crop&q=80",
                channelSubscribers = "620K",
                likesCount = 12800,
                category = "Tech & Coding",
                tags = listOf("Kotlin", "JetpackCompose", "Architecture", "Android"),
                aiSummary = "Highlights best practices for separating UI representations from domain state, avoiding recomposition churn through immutable data classes.",
                transcript = "0:00 Architecture overview\n1:00 StateFlow and UI State\n2:30 Safe composable previews\n3:30 Testing",
                chapters = listOf(
                    VideoChapter("Overview", 0, "0:00"),
                    VideoChapter("StateFlow Patterns", 60, "1:00"),
                    VideoChapter("Composable Previews", 150, "2:30")
                ),
                commentsCount = 318,
                isLiked = false,
                isSubscribed = true
            ),
            Video(
                id = "vid_4",
                title = "Next-Gen Cyberpunk Game Trailer: Unreal Engine 5.5 Ray Tracing",
                description = "World premiere cinematic gameplay showcasing Nanite 2 geometry, Lumen software raytracing, and neural upscaling at steady 4K 120 FPS.",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&auto=format&fit=crop&q=80",
                durationSeconds = 734,
                durationFormatted = "12:14",
                viewsCount = 1250000,
                publishedAt = "5 days ago",
                channelId = "ch_techpulse",
                channelTitle = "TechPulse Studio",
                channelAvatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=200&auto=format&fit=crop&q=80",
                channelSubscribers = "1.42M",
                likesCount = 112000,
                category = "Gaming",
                tags = listOf("UnrealEngine", "Gaming", "RayTracing", "Cyberpunk"),
                aiSummary = "Cinematic reveal of real-time lighting advancements utilizing dynamic voxel cone tracing in futuristic metropolis environments.",
                transcript = "Official reveal footage showcasing next generation graphics technologies.",
                chapters = listOf(
                    VideoChapter("Cinematic Intro", 0, "0:00"),
                    VideoChapter("Gameplay Showcase", 180, "3:00"),
                    VideoChapter("Boss Encounter", 450, "7:30")
                ),
                commentsCount = 2840,
                isLiked = true,
                isSubscribed = true
            ),
            Video(
                id = "vid_5",
                title = "Cinematic Drone Journey Through Iceland's Black Sand Beaches & Glaciers",
                description = "Captured in ProRes 422 HQ 60fps with aerial FPV drones across Vik, Diamond Beach, and the Highlands.",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1504893524553-b855bce32c67?w=800&auto=format&fit=crop&q=80",
                durationSeconds = 180,
                durationFormatted = "3:00",
                viewsCount = 420000,
                publishedAt = "2 weeks ago",
                channelId = "ch_soundwave",
                channelTitle = "Vibe Beats & Lo-Fi",
                channelAvatarUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=200&auto=format&fit=crop&q=80",
                channelSubscribers = "890K",
                likesCount = 38900,
                category = "Design",
                tags = listOf("Travel", "Drone", "Iceland", "Cinematic"),
                aiSummary = "A visual exploration of sub-arctic geological formations, geothermal vents, and glacial runoff channels.",
                transcript = "Atmospheric soundtrack accompanied by nature sounds.",
                chapters = emptyList(),
                commentsCount = 512,
                isLiked = false,
                isSubscribed = false
            )
        )
        _videos.value = initialVideos

        val initialShorts = listOf(
            ShortItem(
                id = "short_1",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                title = "3 Android Compose tricks that feel like magic! 🔥 #Shorts #AndroidDev",
                channelTitle = "TechPulse Studio",
                channelAvatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=200&auto=format&fit=crop&q=80",
                musicTitle = "Original Audio - TechPulse Studio",
                likesCount = 89400,
                commentsCount = 1420,
                isLiked = true,
                isSubscribed = true
            ),
            ShortItem(
                id = "short_2",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                title = "How YouTube stores millions of hours of 4K video 🤯 Server Architecture",
                channelTitle = "FullStack Forge",
                channelAvatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=200&auto=format&fit=crop&q=80",
                musicTitle = "Cyber Vibe Beats - Neon Night",
                likesCount = 124000,
                commentsCount = 2100,
                isLiked = false,
                isSubscribed = true
            ),
            ShortItem(
                id = "short_3",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                title = "POV: Coding in a glass cabin in Norway during blizzard ☕💻",
                channelTitle = "Vibe Beats & Lo-Fi",
                channelAvatarUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=200&auto=format&fit=crop&q=80",
                musicTitle = "Rain on Roof - Ambient Chill",
                likesCount = 312000,
                commentsCount = 4820,
                isLiked = false,
                isSubscribed = false
            )
        )
        _shorts.value = initialShorts

        val initialComments = listOf(
            Comment(
                id = "c_1",
                videoId = "vid_1",
                authorName = "Marcus Chen",
                authorAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80",
                text = "The breakdown of signed S3 URLs vs server proxy upload is hands down the most practical explanation on VibeTube. Subscribed!",
                likesCount = 124,
                isLiked = true,
                timeAgo = "1 day ago"
            ),
            Comment(
                id = "c_2",
                videoId = "vid_1",
                authorName = "Sarah Jenkins",
                authorAvatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=200&auto=format&fit=crop&q=80",
                text = "Are you planning to cover WebRTC livestreaming next? Would love to see peer-to-peer playback handling.",
                likesCount = 47,
                isLiked = false,
                timeAgo = "18 hours ago"
            ),
            Comment(
                id = "c_3",
                videoId = "vid_1",
                authorName = "DevDan",
                authorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&auto=format&fit=crop&q=80",
                text = "The Media3 ExoPlayer caching configuration solved our app's stutter completely. Huge thanks!",
                likesCount = 89,
                isLiked = false,
                timeAgo = "12 hours ago"
            )
        )
        _commentsMap.value = mapOf("vid_1" to initialComments)

        val initialPlaylists = listOf(
            Playlist(
                id = "pl_watch_later",
                title = "Watch Later",
                videoCount = 2,
                isPrivate = true,
                thumbnailResUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=400&auto=format&fit=crop&q=80",
                videoIds = mutableListOf("vid_3", "vid_4")
            ),
            Playlist(
                id = "pl_liked",
                title = "Liked Videos",
                videoCount = 2,
                isPrivate = true,
                thumbnailResUrl = "https://images.unsplash.com/photo-1574717024653-61fd2cf4d44d?w=400&auto=format&fit=crop&q=80",
                videoIds = mutableListOf("vid_1", "vid_4")
            ),
            Playlist(
                id = "pl_coding",
                title = "Architecture & High Scale",
                videoCount = 2,
                isPrivate = false,
                thumbnailResUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=400&auto=format&fit=crop&q=80",
                videoIds = mutableListOf("vid_1", "vid_3")
            )
        )
        _playlists.value = initialPlaylists
        _watchHistory.value = listOf(initialVideos[0], initialVideos[1])
    }

    fun toggleVideoLike(videoId: String): Video? {
        val current = _videos.value.toMutableList()
        val index = current.indexOfFirst { it.id == videoId }
        if (index != -1) {
            val v = current[index]
            val newIsLiked = !v.isLiked
            val updated = v.copy(
                isLiked = newIsLiked,
                likesCount = if (newIsLiked) v.likesCount + 1 else (v.likesCount - 1).coerceAtLeast(0),
                isDisliked = false
            )
            current[index] = updated
            _videos.value = current
            return updated
        }
        return null
    }

    fun toggleVideoDislike(videoId: String): Video? {
        val current = _videos.value.toMutableList()
        val index = current.indexOfFirst { it.id == videoId }
        if (index != -1) {
            val v = current[index]
            val newIsDisliked = !v.isDisliked
            val updated = v.copy(
                isDisliked = newIsDisliked,
                isLiked = false,
                likesCount = if (v.isLiked) (v.likesCount - 1).coerceAtLeast(0) else v.likesCount
            )
            current[index] = updated
            _videos.value = current
            return updated
        }
        return null
    }

    fun toggleSubscription(channelId: String): Boolean {
        val channelsMap = _channels.value.toMutableMap()
        val ch = channelsMap[channelId] ?: return false
        val newSubState = !ch.isSubscribed
        channelsMap[channelId] = ch.copy(isSubscribed = newSubState)
        _channels.value = channelsMap

        // Update in video list
        val updatedVideos = _videos.value.map {
            if (it.channelId == channelId) it.copy(isSubscribed = newSubState) else it
        }
        _videos.value = updatedVideos
        return newSubState
    }

    fun addToHistory(video: Video) {
        val current = _watchHistory.value.toMutableList()
        current.removeAll { it.id == video.id }
        current.add(0, video)
        _watchHistory.value = current.take(20)
    }

    fun clearHistory() {
        _watchHistory.value = emptyList()
    }

    fun addComment(videoId: String, text: String): Comment {
        val user = _currentUser.value
        val newComment = Comment(
            id = "c_${System.currentTimeMillis()}",
            videoId = videoId,
            authorName = user?.name ?: "You",
            authorAvatarUrl = user?.avatarUrl ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
            text = text,
            likesCount = 0,
            isLiked = false,
            timeAgo = "Just now"
        )
        val currentMap = _commentsMap.value.toMutableMap()
        val existingList = (currentMap[videoId] ?: emptyList()).toMutableList()
        existingList.add(0, newComment)
        currentMap[videoId] = existingList
        _commentsMap.value = currentMap

        // Increment commentsCount in video
        val videosList = _videos.value.toMutableList()
        val vIdx = videosList.indexOfFirst { it.id == videoId }
        if (vIdx != -1) {
            val v = videosList[vIdx]
            videosList[vIdx] = v.copy(commentsCount = v.commentsCount + 1)
            _videos.value = videosList
        }

        return newComment
    }

    fun toggleCommentLike(videoId: String, commentId: String) {
        val currentMap = _commentsMap.value.toMutableMap()
        val list = (currentMap[videoId] ?: emptyList()).toMutableList()
        val idx = list.indexOfFirst { it.id == commentId }
        if (idx != -1) {
            val c = list[idx]
            val newLiked = !c.isLiked
            list[idx] = c.copy(
                isLiked = newLiked,
                likesCount = if (newLiked) c.likesCount + 1 else (c.likesCount - 1).coerceAtLeast(0)
            )
            currentMap[videoId] = list
            _commentsMap.value = currentMap
        }
    }

    fun createPlaylist(title: String, isPrivate: Boolean): Playlist {
        val newPl = Playlist(
            id = "pl_${System.currentTimeMillis()}",
            title = title,
            videoCount = 0,
            isPrivate = isPrivate,
            thumbnailResUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&auto=format&fit=crop&q=80"
        )
        val current = _playlists.value.toMutableList()
        current.add(newPl)
        _playlists.value = current
        return newPl
    }

    fun addVideoToPlaylist(playlistId: String, videoId: String) {
        val current = _playlists.value.toMutableList()
        val idx = current.indexOfFirst { it.id == playlistId }
        if (idx != -1) {
            val pl = current[idx]
            if (!pl.videoIds.contains(videoId)) {
                pl.videoIds.add(videoId)
                val updated = pl.copy(videoCount = pl.videoIds.size)
                current[idx] = updated
                _playlists.value = current
            }
        }
    }

    fun publishUploadedVideo(
        title: String,
        description: String,
        category: String,
        visibility: VideoVisibility,
        tags: List<String>
    ): Video {
        val user = _currentUser.value
        val newVid = Video(
            id = "vid_${System.currentTimeMillis()}",
            title = title,
            description = description,
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800&auto=format&fit=crop&q=80",
            durationSeconds = 180,
            durationFormatted = "3:00",
            viewsCount = 1,
            publishedAt = "Just now",
            channelId = "ch_mychannel",
            channelTitle = user?.name ?: "My Channel",
            channelAvatarUrl = user?.avatarUrl ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
            channelSubscribers = "1",
            likesCount = 1,
            category = category,
            tags = tags,
            aiSummary = "AI Summary: Video uploaded successfully. Initial automatic transcription processed.",
            transcript = "0:00 Welcome to this new video on VibeTube.",
            visibility = visibility
        )
        val current = _videos.value.toMutableList()
        current.add(0, newVid)
        _videos.value = current
        return newVid
    }

    fun toggleShortLike(shortId: String) {
        val list = _shorts.value.toMutableList()
        val idx = list.indexOfFirst { it.id == shortId }
        if (idx != -1) {
            val s = list[idx]
            val newLiked = !s.isLiked
            list[idx] = s.copy(
                isLiked = newLiked,
                likesCount = if (newLiked) s.likesCount + 1 else (s.likesCount - 1).coerceAtLeast(0)
            )
            _shorts.value = list
        }
    }

    fun toggleShortSubscribe(shortId: String) {
        val list = _shorts.value.toMutableList()
        val idx = list.indexOfFirst { it.id == shortId }
        if (idx != -1) {
            val s = list[idx]
            list[idx] = s.copy(isSubscribed = !s.isSubscribed)
            _shorts.value = list
        }
    }
}

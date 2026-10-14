package com.example.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

// DTOs matching Laravel Sanctum API responses
data class AuthRequest(
    val email: String,
    val password: String,
    val name: String? = null
)

data class AuthResponse(
    val token: String,
    val user: UserDto,
    val message: String? = null
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    @Json(name = "avatar_url") val avatarUrl: String? = null
)

data class VideoDto(
    val id: String,
    val title: String,
    val description: String,
    @Json(name = "video_url") val videoUrl: String,
    @Json(name = "thumbnail_url") val thumbnailUrl: String,
    @Json(name = "duration_seconds") val durationSeconds: Long,
    @Json(name = "views_count") val viewsCount: Long,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "channel_title") val channelTitle: String,
    @Json(name = "channel_avatar") val channelAvatar: String,
    val category: String
)

data class PaginatedVideosResponse(
    val data: List<VideoDto>,
    @Json(name = "current_page") val currentPage: Int,
    @Json(name = "last_page") val lastPage: Int
)

data class UploadSignedUrlResponse(
    @Json(name = "upload_url") val uploadUrl: String,
    @Json(name = "video_key") val videoKey: String,
    @Json(name = "expires_in") val expiresIn: Int
)

data class CreateVideoRequest(
    val title: String,
    val description: String,
    val category: String,
    val visibility: String,
    @Json(name = "video_key") val videoKey: String
)

interface VibeTubeApiService {
    @POST("auth/register")
    suspend fun register(@Body request: AuthRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Map<String, String>

    @GET("videos")
    suspend fun getVideos(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1
    ): PaginatedVideosResponse

    @GET("videos/shorts")
    suspend fun getShorts(): List<VideoDto>

    @POST("videos/{id}/like")
    suspend fun toggleLike(
        @Path("id") videoId: String,
        @Header("Authorization") token: String
    ): Map<String, Any>

    @POST("upload/signed-url")
    suspend fun getSignedUploadUrl(
        @Header("Authorization") token: String,
        @Query("filename") filename: String,
        @Query("content_type") contentType: String
    ): UploadSignedUrlResponse

    @POST("videos")
    suspend fun publishVideo(
        @Header("Authorization") token: String,
        @Body request: CreateVideoRequest
    ): VideoDto
}

object ApiClient {
    // Default points to Android Emulator host mapping for local Laravel server (http://10.0.2.2:8000/api/)
    var baseUrl: String = "http://10.0.2.2:8000/api/"
        set(value) {
            field = if (value.endsWith("/")) value else "$value/"
            retrofitInstance = null
        }

    private var retrofitInstance: Retrofit? = null

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    fun getService(): VibeTubeApiService {
        if (retrofitInstance == null) {
            retrofitInstance = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
        }
        return retrofitInstance!!.create(VibeTubeApiService::class.java)
    }
}

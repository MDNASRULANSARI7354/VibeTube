<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\VideoController;
use App\Http\Controllers\Api\ShortsController;
use App\Http\Controllers\Api\UploadController;

/*
|--------------------------------------------------------------------------
| VibeTube API Routes (Sanctum Authenticated)
|--------------------------------------------------------------------------
*/

// Public Authentication Endpoints
Route::prefix('auth')->group(function () {
    Route::post('/register', [AuthController::class, 'register']);
    Route::post('/login', [AuthController::class, 'login']);
});

// Public Discovery Endpoints
Route::get('/videos', [VideoController::class, 'index']);
Route::get('/videos/shorts', [ShortsController::class, 'index']);
Route::get('/videos/{id}', [VideoController::class, 'show']);

// Protected User & Creator Endpoints (Bearer Token Required)
Route::middleware('auth:sanctum')->group(function () {
    // Auth & Profile
    Route::prefix('auth')->group(function () {
        Route::get('/me', [AuthController::class, 'me']);
        Route::post('/logout', [AuthController::class, 'logout']);
    });

    // Video Interactions
    Route::post('/videos/{id}/like', [VideoController::class, 'toggleLike']);

    // Video Ingestion & S3 Pre-signed Upload
    Route::post('/upload/signed-url', [UploadController::class, 'getSignedUploadUrl']);
    Route::post('/upload/publish', [UploadController::class, 'publish']);
});

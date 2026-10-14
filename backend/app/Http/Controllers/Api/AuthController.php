<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\User;
use App\Models\CreatorProfile;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Str;
use Illuminate\Validation\ValidationException;

class AuthController extends Controller
{
    public function register(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:100',
            'email' => 'required|string|email|max:255|unique:users',
            'password' => 'required|string|min:8',
            'handle' => 'nullable|string|max:50|unique:users',
        ]);

        $handle = $validated['handle'] ?? '@' . Str::slug($validated['name']) . rand(100, 999);

        $user = User::create([
            'name' => $validated['name'],
            'email' => $validated['email'],
            'password' => Hash::make($validated['password']),
            'handle' => $handle,
            'avatar_url' => "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
            'role' => 'creator',
        ]);

        // Automatically provision creator channel profile
        CreatorProfile::create([
            'user_id' => $user->id,
            'channel_name' => $user->name,
            'handle' => $user->handle,
            'avatar_url' => $user->avatar_url,
            'bio' => 'New VibeTube Creator',
        ]);

        $token = $user->createToken('vibetube_android_token')->plainTextToken;

        return response()->json([
            'message' => 'User registered successfully',
            'token' => $token,
            'user' => [
                'id' => $user->id,
                'name' => $user->name,
                'email' => $user->email,
                'handle' => $user->handle,
                'avatar_url' => $user->avatar_url,
            ]
        ], 201);
    }

    public function login(Request $request)
    {
        $request->validate([
            'email' => 'required|email',
            'password' => 'required',
        ]);

        $user = User::where('email', $request->email)->first();

        if (!$user || !Hash::check($request->password, $user->password)) {
            throw ValidationException::withMessages([
                'email' => ['The provided credentials do not match our records.'],
            ]);
        }

        // Revoke old tokens for single-session security if desired, or allow multiple devices
        $token = $user->createToken('vibetube_android_token')->plainTextToken;

        return response()->json([
            'token' => $token,
            'user' => [
                'id' => $user->id,
                'name' => $user->name,
                'email' => $user->email,
                'handle' => $user->handle,
                'avatar_url' => $user->avatar_url,
            ]
        ]);
    }

    public function me(Request $request)
    {
        $user = $request->user()->load('creatorProfile');
        return response()->json($user);
    }

    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();

        return response()->json([
            'message' => 'Successfully logged out and token revoked'
        ]);
    }
}

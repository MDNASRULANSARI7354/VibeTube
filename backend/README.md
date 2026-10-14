# VibeTube Backend — Laravel 11, PostgreSQL & S3 Streaming Architecture

This is the production-ready backend for **VibeTube**, providing high-performance RESTful APIs, token-based authentication via **Laravel Sanctum**, video ingestion via **S3-compatible signed URLs**, and background **FFmpeg** adaptive HLS transcoding.

---

## System Requirements

- **PHP**: 8.2 or 8.3 with extensions (`pdo_pgsql`, `pgsql`, `mbstring`, `openssl`, `curl`)
- **Composer**: 2.x
- **PostgreSQL**: 15 or 16
- **Object Storage**: AWS S3 or MinIO (S3-compatible local server)
- **FFmpeg & FFprobe**: Version 5.x or 6.x installed on the system path
- **Node.js & NPM**: (Optional, for Blade dashboard assets)

---

## 1. Quickstart with Docker (Recommended)

To spin up PHP, Nginx, PostgreSQL, and MinIO S3 with a single command:

```bash
cd backend
docker-compose up -d
docker-compose exec app php artisan migrate --seed
docker-compose exec app php artisan key:generate
```

- API Base URL: `http://localhost:8000/api`
- MinIO S3 Console: `http://localhost:9001` (User: `minioadmin` / Pass: `minioadmin`)

---

## 2. Windows Setup (PowerShell / CMD)

### Step 1: Install Dependencies & PostgreSQL
1. Install PHP 8.3 and Composer from [getcomposer.org](https://getcomposer.org/).
2. Install PostgreSQL from [postgresql.org](https://www.postgresql.org/download/windows/) and start the service.
3. Install FFmpeg using `winget install Gyan.FFmpeg` or extract to `C:\ffmpeg\bin` and add to system PATH.

### Step 2: Configure Environment
```powershell
cd backend
copy .env.example .env
composer install
php artisan key:generate
```

Edit `.env` to match your local PostgreSQL credentials:
```ini
DB_CONNECTION=pgsql
DB_HOST=127.0.0.1
DB_PORT=5432
DB_DATABASE=vibetube
DB_USERNAME=postgres
DB_PASSWORD=YOUR_PASSWORD
```

### Step 3: Run Migrations & Seed Data
```powershell
php artisan migrate --seed
```

### Step 4: Run FFmpeg Queue Worker & Dev Server
Open two terminal windows:

Terminal 1 (HTTP Server):
```powershell
php artisan serve --host=0.0.0.0 --port=8000
```

Terminal 2 (Queue Worker for FFmpeg Video Processing):
```powershell
php artisan queue:work --timeout=3600
```

---

## 3. Linux / macOS Setup

```bash
cd backend
cp .env.example .env
composer install
php artisan key:generate
php artisan migrate --seed
php artisan serve --host=0.0.0.0 --port=8000
```

---

## 4. Connecting the Android App

When running the Android emulator in AI Studio or Android Studio:
- **Android Emulator to Localhost**: Use `http://10.0.2.2:8000/api/` (Android maps `10.0.2.2` to host machine `127.0.0.1`).
- **Physical Device**: Use your computer's local Wi-Fi IP address, e.g. `http://192.168.1.100:8000/api/`.
- In VibeTube Android app, tap your **Avatar Profile -> Settings** icon to test and configure the live endpoint!

---

## 5. Core API Endpoints

### Authentication (Sanctum)
- `POST /api/auth/register` — Register new user and auto-create creator channel.
  ```json
  { "name": "John Doe", "email": "john@vibetube.io", "password": "Password123!" }
  ```
- `POST /api/auth/login` — Login and receive Bearer Token.
- `GET /api/auth/me` (Auth required) — Get active profile and channel stats.
- `POST /api/auth/logout` (Auth required) — Revoke active Sanctum bearer token.

### Video Feeds & Playback
- `GET /api/videos` — Paginated home feed. Optional query params: `?category=Tech%20%26%20Coding`, `?search=Compose`, `?page=1`.
- `GET /api/videos/shorts` — Curated vertical 9:16 Shorts feed.
- `GET /api/videos/{id}` — Video details with chapters, captions, HLS asset renditions, and telemetry view tracking.
- `POST /api/videos/{id}/like` (Auth required) — Toggle like/dislike.

### Video Upload Flow
1. `POST /api/upload/signed-url` (Auth required) — Request secure, time-limited S3 pre-signed upload URL.
2. Direct binary HTTP PUT from mobile client to S3 bucket.
3. `POST /api/upload/publish` (Auth required) — Trigger backend FFmpeg background transcoding and database publication.

---

## 6. Running Automated Tests

Run backend feature tests:
```bash
php artisan test
```

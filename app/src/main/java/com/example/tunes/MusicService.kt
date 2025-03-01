package com.example.tunes

import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private val CHANNEL_ID = "music_service_channel"
    private var currentTrackResId: Int = R.raw.song1  // Default track

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())  // Ensure foreground service starts first

        val action = intent?.action

        when (action) {
            "PLAY" -> {
                val trackResId = intent.getIntExtra("TRACK_ID", currentTrackResId)
                if (trackResId != currentTrackResId) {
                    currentTrackResId = trackResId
                    mediaPlayer?.release()
                    mediaPlayer = MediaPlayer.create(this, currentTrackResId)
                }

                if (mediaPlayer?.isPlaying == false) {
                    mediaPlayer?.start()
                }
            }
            "PAUSE" -> {
                mediaPlayer?.pause()
            }
            "STOP" -> {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                stopSelf()
            }
        }

        return START_STICKY
    }

    private fun createNotification(): Notification {
        val playIntent = Intent(this, MusicService::class.java).apply { action = "PLAY" }
        val pauseIntent = Intent(this, MusicService::class.java).apply { action = "PAUSE" }
        val stopIntent = Intent(this, MusicService::class.java).apply { action = "STOP" }

        val playPending = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_IMMUTABLE)
        val pausePending = PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_IMMUTABLE)
        val stopPending = PendingIntent.getService(this, 2, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText("Playing music...")
            .setSmallIcon(R.drawable.ic_music)
            .addAction(R.drawable.ic_play, "Play", playPending)
            .addAction(R.drawable.ic_pause, "Pause", pausePending)
            .addAction(R.drawable.ic_stop, "Stop", stopPending)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }
}

package com.example.tunes

import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MusicService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private val CHANNEL_ID = "music_service_channel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()  // Create notification channel for Android 8+
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!this::mediaPlayer.isInitialized) {
            mediaPlayer = MediaPlayer.create(this, R.raw.song1)  // Replace with your track
            mediaPlayer.isLooping = true
        }

        val action = intent?.action
        when (action) {
            "PLAY" -> mediaPlayer.start()
            "PAUSE" -> mediaPlayer.pause()
            "STOP" -> {
                mediaPlayer.stop()
                stopForeground(true)  // Properly stop foreground service
                stopSelf()
            }
        }

        val notification = createNotification()
        startForeground(1, notification)
        android.util.Log.d("MusicService", "startForeground() called")  // LOG THIS

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
            .setOngoing(true)  // Prevent user from swiping it away
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

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }
}

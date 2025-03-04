package com.example.tunes

import android.app.*
import android.content.*
import android.media.MediaPlayer
import android.os.*
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat

class MusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private val CHANNEL_ID = "music_service_channel"
    private var currentTrackResId: Int = R.raw.song1  // Default track
    private lateinit var mediaSession: MediaSessionCompat

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaSession = MediaSessionCompat(this, "MusicService")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val trackResId = intent?.getIntExtra("TRACK_ID", currentTrackResId) ?: currentTrackResId

        when (action) {
            "PLAY" -> playMusic(trackResId)
            "PAUSE" -> pauseMusic()
            "STOP" -> stopMusic()
        }

        startForeground(1, createNotification())
        return START_STICKY
    }

    private fun playMusic(trackResId: Int) {
        if (mediaPlayer == null || trackResId != currentTrackResId) {
            mediaPlayer?.release() // Release old player
            currentTrackResId = trackResId
            mediaPlayer = MediaPlayer.create(this, trackResId)
            mediaPlayer?.setOnCompletionListener { stopMusic() } // Stop when track ends
        }

        mediaPlayer?.start()
        updateNotification()
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
        updateNotification()
    }

    private fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        stopSelf()
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
            .setContentText("Now Playing: Track $currentTrackResId")
            .setSmallIcon(R.drawable.ic_music)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .addAction(R.drawable.ic_play, "Play", playPending)
            .addAction(R.drawable.ic_pause, "Pause", pausePending)
            .addAction(R.drawable.ic_stop, "Stop", stopPending)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(1, createNotification())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

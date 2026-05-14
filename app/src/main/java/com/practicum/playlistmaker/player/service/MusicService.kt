package com.practicum.playlistmaker.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.practicum.playlistmaker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

sealed class AudioPlayerState {
    data object Default : AudioPlayerState()
    data object Prepared : AudioPlayerState()
    data class Playing(val progress: String) : AudioPlayerState()
    data class Paused(val progress: String) : AudioPlayerState()
}

interface PlayerServiceControl {
    fun startPlayer()
    fun pausePlayer()
    fun getPlayerState(): StateFlow<AudioPlayerState>
    fun showNotification()
    fun hideNotification()
    fun setTrack(url: String?, name: String?, artist: String?)
}

class MusicService : Service(), PlayerServiceControl {

    private val mediaPlayer = MediaPlayer()
    private var songUrl: String? = null
    private var trackName: String? = null
    private var artistName: String? = null

    private val binder = MusicBinder()

    private val _playerState = MutableStateFlow<AudioPlayerState>(AudioPlayerState.Default)
    private val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

    override fun getPlayerState(): StateFlow<AudioPlayerState> = playerState

    private var timerJob: Job? = null

    inner class MusicBinder : Binder() {
        fun getService(): PlayerServiceControl = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder {
        intent?.let {
            val url = it.getStringExtra(EXTRA_URL)
            val name = it.getStringExtra(EXTRA_TRACK_NAME)
            val artist = it.getStringExtra(EXTRA_ARTIST_NAME)
            setTrack(url, name, artist)
        }
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun setTrack(url: String?, name: String?, artist: String?) {
        if (url != null && url != songUrl) {
            songUrl = url
            trackName = name
            artistName = artist
            preparePlayer()
        }
    }

    private fun preparePlayer() {
        mediaPlayer.reset()
        songUrl?.let {
            mediaPlayer.setDataSource(it)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                _playerState.value = AudioPlayerState.Prepared
            }
            mediaPlayer.setOnCompletionListener {
                _playerState.value = AudioPlayerState.Prepared
                mediaPlayer.seekTo(0)
                stopTimer()
                hideNotification()
            }
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        _playerState.value = AudioPlayerState.Playing(getCurrentPosition())
        startTimer()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        _playerState.value = AudioPlayerState.Paused(getCurrentPosition())
        stopTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer.isPlaying) {
                _playerState.value = AudioPlayerState.Playing(getCurrentPosition())
                delay(TIMER_DELAY)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    private fun getCurrentPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    override fun showNotification() {
        if (_playerState.value is AudioPlayerState.Playing) {
            val notification = createNotification()
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                } else {
                    0
                }
            )
        }
    }

    override fun hideNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("$artistName - $trackName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Music Service Channel"
            val descriptionText = "Channel for music playback"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        stopTimer()
    }

    companion object {
        private const val CHANNEL_ID = "music_service_channel"
        private const val NOTIFICATION_ID = 1
        private const val TIMER_DELAY = 300L

        const val EXTRA_URL = "EXTRA_URL"
        const val EXTRA_TRACK_NAME = "EXTRA_TRACK_NAME"
        const val EXTRA_ARTIST_NAME = "EXTRA_ARTIST_NAME"
    }
}

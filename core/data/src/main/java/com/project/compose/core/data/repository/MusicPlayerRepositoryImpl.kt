package com.project.compose.core.data.repository

import android.content.ComponentName
import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_READY
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.project.compose.core.common.utils.state.PlayerState
import com.project.compose.core.data.service.MusicService
import com.project.compose.core.data.source.MusicSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class MusicPlayerRepositoryImpl @Inject constructor(
    musicSource: MusicSource,
    private val context: Context
) : MusicPlayerRepository {

    private var mediaController: MediaController? = null

    private var positionUpdateJob: Job? = null

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val trackList = musicSource.getTracks()

    private val _playerState = MutableStateFlow(PlayerState(trackList = trackList))
    override val playerState = _playerState.asStateFlow()

    init {
        initController()
    }

    private fun initController() {
        val sessionToken = SessionToken(
            context, ComponentName(context, MusicService::class.java)
        )
        MediaController.Builder(context, sessionToken).buildAsync().apply {
            addListener({
                mediaController = get()
                mediaController?.repeatMode = Player.REPEAT_MODE_ALL
                mediaController?.addListener(playerListener)
                preparePlaylist()
            }, MoreExecutors.directExecutor())
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
            if (isPlaying) startPositionUpdates() else stopPositionUpdates()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val newTrack = trackList.find { it.url == mediaItem?.mediaId }
            _playerState.value = _playerState.value.copy(currentTrack = newTrack)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == STATE_READY) {
                val duration = mediaController?.duration ?: 0L
                if (duration > 0) {
                    _playerState.value = _playerState.value.copy(totalDuration = duration)
                }
            }
        }
    }

    private fun startPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = scope.launch {
            while (isActive) {
                _playerState.value = _playerState.value.copy(
                    currentPosition = mediaController?.currentPosition ?: 0L
                )
                delay(1000)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
    }

    private fun preparePlaylist() {
        val mediaItems = trackList.map { track ->
            MediaItem.Builder()
                .setUri(track.url.toUri())
                .setMediaId(track.url)
                .build()
        }
        mediaController?.setMediaItems(mediaItems)
        mediaController?.prepare()
        _playerState.value = _playerState.value.copy(currentTrack = trackList.firstOrNull())
    }

    override fun play() {
        mediaController?.play()
    }

    override fun pause() {
        mediaController?.pause()
    }

    override fun skipNext() {
        mediaController?.seekToNextMediaItem()
    }

    override fun skipPrevious() {
        mediaController?.seekToPreviousMediaItem()
    }

    override fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    override fun selectTrack(index: Int) {
        mediaController?.seekTo(index, 0)
    }

    override fun release() {
        scope.cancel()
        mediaController?.release()
    }
}
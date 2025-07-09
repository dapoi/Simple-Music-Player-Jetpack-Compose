package com.project.compose.feature.home.viewmodel

import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.project.compose.core.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

// Model data sederhana untuk lagu
data class AudioTrack(
    val title: String,
    val artist: String,
    val url: String
)

@HiltViewModel
class HomeViewModel @Inject constructor() : BaseViewModel() {
    // Ganti dengan URL MP3 Anda yang valid
    val trackList = listOf(
        AudioTrack(
            title = "Al-Fatihah",
            artist = "Misyari Rasyid Al-Afasi",
            url = "https://equran.nos.wjv-1.neo.id/audio-full/Misyari-Rasyid-Al-Afasi/001.mp3"
        ),
        AudioTrack(
            title = "At-Takatsur",
            artist = "Abdullah Al-Juhany",
            url = "https://equran.nos.wjv-1.neo.id/audio-full/Abdullah-Al-Juhany/102.mp3"
        ),
        AudioTrack(
            title = "Al-Asr",
            artist = "Ibrahim Al-Dossari",
            url = "https://equran.nos.wjv-1.neo.id/audio-full/Ibrahim-Al-Dossari/103.mp3"
        ),
    )

    // State untuk UI
    val isPlaying = mutableStateOf(false)
    val currentTrack = mutableStateOf<AudioTrack?>(null)
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()
    val totalDuration = mutableLongStateOf(0L)

    var mediaController: MediaController? = null

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlayingValue: Boolean) {
            super.onIsPlayingChanged(isPlayingValue)
            isPlaying.value = isPlayingValue
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            currentTrack.value = trackList.find { it.url == mediaItem?.mediaId }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                totalDuration.value = mediaController?.duration ?: 0L
            }
        }
    }

    fun setMediaController(controllerFuture: ListenableFuture<MediaController>) {
        controllerFuture.addListener({
            mediaController = controllerFuture.get()
            mediaController?.addListener(playerListener)
            preparePlaylist()
            updatePlaybackState()
        }, MoreExecutors.directExecutor())
    }

    private fun preparePlaylist() {
        val mediaItems = trackList.map { track ->
            MediaItem.Builder()
                .setMediaId(track.url) // Gunakan URL sebagai ID media
                .setUri(track.url.toUri()) // Set URI untuk memutar
                .build()
        }
        mediaController?.setMediaItems(mediaItems)
        mediaController?.prepare()
        currentTrack.value = trackList.firstOrNull()
    }

    private fun updatePlaybackState() {
        viewModelScope.launch {
            while (isActive) {
                _currentPosition.value = mediaController?.currentPosition ?: 0L
                delay(1000) // Update setiap detik
            }
        }
    }

    fun play() = mediaController?.play()

    fun pause() = mediaController?.pause()

    fun skipNext() {
        val currentIndex = mediaController?.currentMediaItemIndex ?: return
        val nextIndex = if (currentIndex == trackList.lastIndex) 0 else currentIndex + 1
        mediaController?.seekTo(nextIndex, 0)
        mediaController?.play()
    }

    fun skipPrevious() {
        val currentIndex = mediaController?.currentMediaItemIndex ?: return
        val previousIndex = if (currentIndex == 0) trackList.lastIndex else currentIndex - 1
        mediaController?.seekTo(previousIndex, 0)
        mediaController?.play()
    }

    fun seekTo(position: Long) = mediaController?.seekTo(position)

    override fun onCleared() {
        super.onCleared()
        mediaController?.removeListener(playerListener)
        mediaController?.release() // Melepaskan controller
        mediaController = null
    }
}
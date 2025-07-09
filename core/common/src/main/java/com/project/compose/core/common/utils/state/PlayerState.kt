package com.project.compose.core.common.utils.state

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val totalDuration: Long = 0L,
    val trackList: List<AudioTrack> = emptyList(),
    val currentTrack: AudioTrack? = null
) {
    data class AudioTrack(
        val title: String,
        val artist: String,
        val url: String
    )
}
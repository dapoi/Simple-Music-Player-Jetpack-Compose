package com.project.compose.core.data.repository

import com.project.compose.core.common.utils.state.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface MusicPlayerRepository {
    val playerState: StateFlow<PlayerState>
    fun play()
    fun pause()
    fun skipNext()
    fun skipPrevious()
    fun seekTo(position: Long)
    fun selectTrack(index: Int)
    fun release()
}
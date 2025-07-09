package com.project.compose.core.data.source

import com.project.compose.core.common.utils.state.PlayerState

interface MusicSource {
    fun getTracks(): List<PlayerState.AudioTrack>
}
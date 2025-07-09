package com.project.compose.feature.home.viewmodel

import com.project.compose.core.common.base.BaseViewModel
import com.project.compose.core.data.repository.MusicPlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val musicPlayerRepository: MusicPlayerRepository
) : BaseViewModel() {

    val playerState = musicPlayerRepository.playerState

    fun play() {
        musicPlayerRepository.play()
    }

    fun pause() {
        musicPlayerRepository.pause()
    }

    fun skipNext() {
        musicPlayerRepository.skipNext()
    }

    fun skipPrevious() {
        musicPlayerRepository.skipPrevious()
    }

    fun seekTo(position: Long) {
        musicPlayerRepository.seekTo(position)
    }

    override fun onCleared() {
        super.onCleared()
        musicPlayerRepository.release()
    }
}
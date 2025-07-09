package com.project.compose.feature.home.screen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.compose.core.common.base.BaseScreen
import com.project.compose.core.data.service.MusicService
import com.project.compose.feature.home.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) = with(viewModel) {
    BaseScreen(showDefaultTopBar = false) {
        HomeContent(this)
    }
}

@Composable
private fun HomeContent(viewModel: HomeViewModel) {
    val context = LocalContext.current
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    var localSliderPosition by remember { mutableFloatStateOf(0f) }
    var isSeeking by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        context.startService(Intent(context, MusicService::class.java))
    }

    LaunchedEffect(playerState.currentPosition, isSeeking) {
        if (!isSeeking) {
            localSliderPosition = playerState.currentPosition.toFloat()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = playerState.currentTrack?.title ?: "No Track Playing",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = playerState.currentTrack?.artist ?: "Unknown Artist",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Slider(
            value = localSliderPosition,
            onValueChange = { newPosition ->
                isSeeking = true
                localSliderPosition = newPosition
            },
            onValueChangeFinished = {
                viewModel.seekTo(localSliderPosition.toLong())
                isSeeking = false
            },
            valueRange = 0f..(if (playerState.totalDuration > 0) playerState.totalDuration.toFloat() else 1f),
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val currentPosText = if (isSeeking) {
                formatDuration(localSliderPosition.toLong())
            } else {
                formatDuration(playerState.currentPosition)
            }
            Text(text = currentPosText)
            Text(text = formatDuration(playerState.totalDuration))
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { viewModel.skipPrevious() }) {
                Text("Previous")
            }
            Button(onClick = { if (playerState.isPlaying) viewModel.pause() else viewModel.play() }) {
                Text(if (playerState.isPlaying) "Pause" else "Play")
            }
            Button(onClick = { viewModel.skipNext() }) {
                Text("Next")
            }
        }
    }
}

@SuppressLint("DefaultLocale")
private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
package com.project.compose.feature.home.screen

import android.annotation.SuppressLint
import android.content.ComponentName
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.project.compose.core.common.base.BaseScreen
import com.project.compose.feature.home.service.PlaybackService
import com.project.compose.feature.home.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) = with(viewModel) {
    BaseScreen {
        HomeContent(this)
    }
}

@Composable
private fun HomeContent(playerViewModel: HomeViewModel) {
    val context = LocalContext.current

    // Efek ini akan berjalan saat composable masuk ke komposisi dan dibersihkan saat keluar
    DisposableEffect(Unit) {
        // Start service terlebih dahulu sebelum membuat controller
        val serviceIntent = Intent(context, PlaybackService::class.java)
        context.startForegroundService(serviceIntent)

        // Beri waktu sejenak untuk service start
        val sessionToken =
            SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        playerViewModel.setMediaController(controllerFuture)

        onDispose {
            // Controller akan di-release di onCleared ViewModel
        }
    }

    val isPlaying by playerViewModel.isPlaying
    val currentTrack by playerViewModel.currentTrack
    val currentPosition by playerViewModel.currentPosition.collectAsState()
    val totalDuration by playerViewModel.totalDuration

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = currentTrack?.title ?: "No Track Playing",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = currentTrack?.artist ?: "Unknown Artist",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(32.dp))

        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { playerViewModel.seekTo(it.toLong()) },
            valueRange = 0f..(if (totalDuration > 0) totalDuration.toFloat() else 1f),
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = formatDuration(currentPosition))
            Text(text = formatDuration(totalDuration))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { playerViewModel.skipPrevious() }) {
                Text("Previous")
            }
            Button(onClick = {
                if (isPlaying) playerViewModel.pause() else playerViewModel.play()
            }) {
                Text(if (isPlaying) "Pause" else "Play")
            }
            Button(onClick = { playerViewModel.skipNext() }) {
                Text("Next")
            }
        }
    }
}

// Helper function untuk format durasi dari milidetik ke MM:SS
@SuppressLint("DefaultLocale")
private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

package com.project.compose.core.data.source

import com.project.compose.core.common.utils.state.PlayerState.AudioTrack
import javax.inject.Inject

class MusicSourceImpl @Inject constructor() : MusicSource {
    override fun getTracks() = listOf(
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
        )
    )
}
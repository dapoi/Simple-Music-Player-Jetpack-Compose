package com.project.compose.core.data.di

import android.content.Context
import com.project.compose.core.data.repository.MusicPlayerRepository
import com.project.compose.core.data.repository.MusicPlayerRepositoryImpl
import com.project.compose.core.data.source.MusicSource
import com.project.compose.core.data.source.MusicSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    fun provideMusicSource(): MusicSource = MusicSourceImpl()

    @Provides
    fun provideMusicPlayerRepository(
        musicSource: MusicSource,
        @ApplicationContext context: Context
    ): MusicPlayerRepository = MusicPlayerRepositoryImpl(musicSource, context)
}
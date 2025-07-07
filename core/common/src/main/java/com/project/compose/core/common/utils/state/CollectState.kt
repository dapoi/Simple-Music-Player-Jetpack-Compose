package com.project.compose.core.common.utils.state

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <T> StateFlow<T>.collectAsStateValue() = collectAsStateWithLifecycle().value
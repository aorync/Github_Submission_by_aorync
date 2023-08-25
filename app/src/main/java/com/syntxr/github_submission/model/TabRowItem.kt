package com.syntxr.github_submission.model

import androidx.compose.runtime.Composable

data class TabRowItem (
    val name: String,
    val layout: @Composable () -> Unit
)
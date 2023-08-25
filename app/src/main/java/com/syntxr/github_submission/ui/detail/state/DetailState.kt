package com.syntxr.github_submission.ui.detail.state

sealed class DetailState{
    data object Loading : DetailState()
    data class Success<T>(val data: T) : DetailState()
    data class Error(val message : String) : DetailState()
}

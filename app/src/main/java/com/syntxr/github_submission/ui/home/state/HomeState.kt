package com.syntxr.github_submission.ui.home.state


sealed class HomeState {
    data object Loading : HomeState()
    data class Success<T>(val data : List<T>) : HomeState()
    data class Error(val message : String) : HomeState()
}

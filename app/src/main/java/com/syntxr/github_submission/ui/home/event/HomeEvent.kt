package com.syntxr.github_submission.ui.home.event


sealed class HomeEvent {
    data object RetrieveUser : HomeEvent()
    data class SearchUser (val query: String) : HomeEvent()
    data object Clear : HomeEvent()
}
package com.syntxr.github_submission.ui.detail.event

sealed class DetailEvent{
    data class RetrieveDetail(val username: String) : DetailEvent()
    data class RetrieveFollowers(val username: String) : DetailEvent()
    data class RetrieveFollowing(val username: String) : DetailEvent()
}

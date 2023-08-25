package com.syntxr.github_submission.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syntxr.github_submission.data.source.remote.service.ApiConfig
import com.syntxr.github_submission.ui.detail.event.DetailEvent
import com.syntxr.github_submission.ui.detail.state.DetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    private val _userDetailState = MutableStateFlow<DetailState?>(null)
    val userDetailState = _userDetailState.asStateFlow()

    private val _userFollowerState = MutableStateFlow<DetailState?>(null)
    val userFollowerState = _userFollowerState.asStateFlow()

    private val _userFollowingState = MutableStateFlow<DetailState?>(null)
    val userFollowingState = _userFollowingState.asStateFlow()


    fun onEvent(event: DetailEvent){
        when (event){
            is DetailEvent.RetrieveDetail -> {
                viewModelScope.launch {
                    _userDetailState.emit(DetailState.Loading)
                    try {
                        val userDetailResult = ApiConfig.getApiService().getUserDetail(event.username)
                        _userDetailState.emit(DetailState.Success(userDetailResult))
                    } catch (e : Exception){
                        _userDetailState.emit(DetailState.Error(e.message ?: ""))
                    }
                }
            }

            is DetailEvent.RetrieveFollowers -> {
                viewModelScope.launch {
                    _userFollowerState.emit(DetailState.Loading)
                    try {
                        val followerResult = ApiConfig.getApiService().getUserFollowers(event.username)
                        _userFollowerState.emit(DetailState.Success(followerResult))
                    }catch (e : Exception){
                        _userFollowerState.emit(DetailState.Error(e.message ?: ""))
                    }
                }
            }
            is DetailEvent.RetrieveFollowing -> {
                viewModelScope.launch {
                    _userFollowingState.emit(DetailState.Loading)
                    try {
                        val followingResult = ApiConfig.getApiService().getUserFollowing(event.username)
                        _userFollowingState.emit(DetailState.Success(followingResult))
                    }catch (e : Exception){
                        _userFollowingState.emit(DetailState.Error(e.message ?: ""))
                    }
                }
            }
        }
    }
}
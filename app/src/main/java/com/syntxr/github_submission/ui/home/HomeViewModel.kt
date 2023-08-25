package com.syntxr.github_submission.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syntxr.github_submission.data.source.remote.service.ApiConfig
import com.syntxr.github_submission.ui.home.event.HomeEvent
import com.syntxr.github_submission.ui.home.state.HomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _listUserState = MutableStateFlow<HomeState?>(null)
    val listUserState = _listUserState.asStateFlow()

    private val _listSearchUserState = MutableStateFlow<HomeState?>(null)
    val listSearchUserState = _listSearchUserState.asStateFlow()

    fun onEvent(event: HomeEvent){
        when (event){
            is HomeEvent.RetrieveUser -> {
                viewModelScope.launch {
                    _listUserState.emit(HomeState.Loading)
                    try {
                        val userResult = ApiConfig.getApiService().getListUser()
                        _listUserState.emit(HomeState.Success(userResult))
                    }catch (e : Exception){
                        _listUserState.emit(HomeState.Error(e.message ?: ""))
                        Log.d("CHECK LIST USER", e.toString())
                    }
                }
            }

            is HomeEvent.SearchUser -> {
                viewModelScope.launch {
                    _listSearchUserState.emit(HomeState.Loading)
                    try {
                        val searchResult = ApiConfig.getApiService().searchUserByName(q = event.query)
                        _listSearchUserState.emit(HomeState.Success(searchResult.items))
                        Log.d("CHECK RESULT SEARCH", searchResult.items.toString())
                    }catch (e : Exception){
                        _listSearchUserState.emit(HomeState.Error(e.message ?: ""))
                    }
                }
            }

            HomeEvent.Clear -> {
                viewModelScope.launch {
                    _listSearchUserState.value = null
                }
            }
        }
    }
}
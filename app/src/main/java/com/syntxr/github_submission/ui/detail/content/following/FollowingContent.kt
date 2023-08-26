package com.syntxr.github_submission.ui.detail.content.following

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.syntxr.github_submission.data.source.remote.response.user.UserResponse
import com.syntxr.github_submission.ui.component.UserItem
import com.syntxr.github_submission.ui.detail.state.DetailState

@Composable
fun FollowingContent(
    followingState: DetailState?,
    navigate: (username: String) -> Unit,
) {
    val snackBarHostState = SnackbarHostState()
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) {
        when (followingState) {
            is DetailState.Error -> {
                LaunchedEffect(key1 = true) {
                    snackBarHostState.showSnackbar(followingState.message)
                }
            }

            DetailState.Loading -> {
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(28.dp))
                    CircularProgressIndicator()
                }
            }

            is DetailState.Success<*> -> {
                val follower = followingState.data as UserResponse
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    items(follower) { user ->
                        UserItem(
                            imgUrl = user.avatarUrl.toString(),
                            name = user.login.toString(),
                            onClick = {
                                navigate(user.login.toString())
                            }
                        )
                    }
                }
            }

            null -> {}
        }
    }
}
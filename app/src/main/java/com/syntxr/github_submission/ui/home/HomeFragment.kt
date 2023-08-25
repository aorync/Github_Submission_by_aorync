package com.syntxr.github_submission.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.syntxr.github_submission.R
import com.syntxr.github_submission.data.source.remote.response.search.Item
import com.syntxr.github_submission.data.source.remote.response.user.UserResponseItem
import com.syntxr.github_submission.ui.component.UserItem
import com.syntxr.github_submission.ui.detail.DetailFragment
import com.syntxr.github_submission.ui.home.event.HomeEvent
import com.syntxr.github_submission.ui.home.state.HomeState

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                HomeScreen(
                    navController = findNavController()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    navController: NavController,
) {
    val listUserState by viewModel.listUserState.collectAsState()
    val listSearchUserState by viewModel.listSearchUserState.collectAsState()

    var queryState by remember {
        mutableStateOf("")
    }

    var activeState by remember {
        mutableStateOf(false)
    }

    val snackBarHostState = SnackbarHostState()

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(HomeEvent.RetrieveUser)
    }

    Column {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Github User",
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(Color.Blue)
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState)
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SearchBar(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(8.dp),
                    query = queryState,
                    onQueryChange = { query ->
                        queryState = query
                    },
                    onSearch = { query ->
                        activeState = true
                        viewModel.onEvent(HomeEvent.SearchUser(query))
                        Log.d("QUERY@APA", query)

                    },
                    active = activeState,
                    onActiveChange = { active ->
                        activeState = active
                    },
                    placeholder = {
                        Text(text = "Search user...")
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (activeState) {
                            IconButton(onClick = {
                                if (queryState.isNotEmpty()) {
                                    queryState = ""
                                } else {
                                    activeState = false
                                    viewModel.onEvent(HomeEvent.Clear)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "btn_close"
                                )
                            }
                        }
                    }
                ) {
                    when (val state = listSearchUserState) {
                        is HomeState.Error -> {
                            LaunchedEffect(key1 = true) {
                                snackBarHostState.showSnackbar(state.message)
                            }
                        }

                        is HomeState.Loading -> {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Spacer(modifier = Modifier.height(28.dp))
                                CircularProgressIndicator()
                            }
                        }

                        is HomeState.Success<*> -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(state.data) { data ->
                                    val user = data as Item
                                    UserItem(
                                        name = user.login,
                                        imgUrl = user.avatarUrl,
                                        onClick = {
                                            val bundle = bundleOf(
                                                DetailFragment.KEY to user.login
                                            )
                                            navController.navigate(R.id.nav_detail, bundle)
                                        }
                                    )
                                }
                            }
                        }

                        null -> {}
                    }
                }


                when (val state = listUserState) {
                    is HomeState.Error -> {
                        LaunchedEffect(key1 = true) {
                            snackBarHostState.showSnackbar(state.message)
                        }
                    }

                    HomeState.Loading -> {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.height(28.dp))
                            CircularProgressIndicator()
                        }
                    }

                    is HomeState.Success<*> -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Log.d("ERROR PRH@", state.data.toString())
                            items(state.data) { data ->
                                val user = data as UserResponseItem
                                UserItem(
                                    imgUrl = user.avatarUrl,
                                    name = user.login,
                                    onClick = {
                                        val bundle = bundleOf(
                                            DetailFragment.KEY to user.login
                                        )
                                        navController.navigate(R.id.nav_detail, bundle)
                                    }
                                )
                            }
                        }
                    }

                    null -> {}
                }


            }
        }
    }
}


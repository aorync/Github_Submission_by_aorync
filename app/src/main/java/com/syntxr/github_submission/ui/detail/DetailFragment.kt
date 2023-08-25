package com.syntxr.github_submission.ui.detail

import android.os.Bundle
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.syntxr.github_submission.R
import com.syntxr.github_submission.data.source.remote.response.detail.UserDetailResponse
import com.syntxr.github_submission.model.TabRowItem
import com.syntxr.github_submission.ui.detail.content.follower.FollowerContent
import com.syntxr.github_submission.ui.detail.content.following.FollowingContent
import com.syntxr.github_submission.ui.detail.event.DetailEvent
import com.syntxr.github_submission.ui.detail.state.DetailState
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val username = arguments?.getString(KEY)
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                DetailScreen(
                    username = username,
                    navController = findNavController()
                )
            }
        }
    }

    companion object {
        const val KEY = "username"
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalPagerApi::class
)
@Composable
fun DetailScreen(
    username: String?,
    viewModel: DetailViewModel = viewModel(),
    navController: NavController,
) {
    val detailState by viewModel.userDetailState.collectAsState()
    val listFollowerState by viewModel.userFollowerState.collectAsState()
    val listFollowingState by viewModel.userFollowingState.collectAsState()

    val snackBarHostState = SnackbarHostState()

    val tabItemList = listOf(
        TabRowItem(
            name = "Followers",
            layout = {
                FollowerContent(
                    followerState = listFollowerState,
                    navigate = { username ->
                        val bundle = bundleOf(
                            DetailFragment.KEY to username
                        )
                        navController.navigate(R.id.nav_detail, bundle)
                    }
                )
            }
        ),
        TabRowItem(
            name = "Following",
            layout = {
                FollowingContent(
                    followingState = listFollowingState,
                    navigate = { username ->
                        val bundle = bundleOf(
                            DetailFragment.KEY to username
                        )
                        navController.navigate(R.id.nav_detail, bundle)
                    }
                )
            }
        )
    )

    val pagerState = rememberPagerState()
    val coroutine = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(DetailEvent.RetrieveDetail(username!!))
        viewModel.onEvent(DetailEvent.RetrieveFollowers(username))
        viewModel.onEvent(DetailEvent.RetrieveFollowing(username))
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "btn_back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Color.Blue)
            )
        }
    ) {
        when (val state = detailState) {
            is DetailState.Error -> {
                LaunchedEffect(key1 = true){
                    snackBarHostState.showSnackbar(state.message)
                }
            }
            DetailState.Loading -> {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(28.dp))
                    CircularProgressIndicator()
                }
            }

            is DetailState.Success<*> -> {
                val detail = state.data as UserDetailResponse
                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    AsyncImage(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        model = detail.avatarUrl,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = detail.login,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = detail.name,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Text(
                            text = "${detail.followers} Followers",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(64.dp))
                        Text(
                            text = "${detail.following} Following",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                            )
                        },
                        backgroundColor = Color.White
                    ) {
                        tabItemList.forEachIndexed { index, tab ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutine.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                text = {
                                    Text(text = tab.name)
                                }
                            )
                        }
                    }
                    HorizontalPager(
                        count = tabItemList.size,
                        state = pagerState
                    ) {
                        tabItemList[pagerState.currentPage].layout()
                    }
                }
            }

            null -> {}
        }

    }
}
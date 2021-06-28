package com.github.jetbrains.rssreader.androidApp.ui.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.jetbrains.rssreader.androidApp.R.string
import com.github.jetbrains.rssreader.androidApp.Screens
import com.github.jetbrains.rssreader.app.FeedAction
import com.github.jetbrains.rssreader.app.FeedStore
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.android.launch
import com.github.terrakok.modo.forward
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    store: FeedStore,
    modo: Modo
) {
    AppTheme {
        ProvideWindowInsets {
            val state = store.observeState().collectAsState()
            val posts = remember(state.value.feeds, state.value.selectedFeedUrl) {
                state.value.feeds.firstOrNull {
                    it.sourceUrl == state.value.selectedFeedUrl
                }?.posts ?: state.value.feeds.flatMap { it.posts }
                    .sortedByDescending { it.date }
            }

            Box {
                val coroutineScope = rememberCoroutineScope()
                val listState = rememberLazyListState()
                val showAddDialog = remember { mutableStateOf(false) }
                SwipeRefresh(
                    state = rememberSwipeRefreshState(state.value.progress),
                    onRefresh = { store.dispatch(FeedAction.Refresh(true)) }
                ) {
                    Column {
                        if (posts.isEmpty()) {
                            Spacer(Modifier.statusBarsHeight())
                            Text(
                                modifier = Modifier.weight(1f).fillMaxWidth().padding(16.dp),
                                text = stringResource(string.no_posts)
                            )
                        }

                        PostList(
                            modifier = Modifier.weight(1f),
                            posts = posts,
                            listState = listState
                        ) { post ->
                            post.link?.let { modo.launch(Screens.Browser(it)) }
                        }

                        //TODO: improvement: hide on scroll down
                        MainFeedBottomBar(
                            feeds = state.value.feeds.sortedByDescending { it.title },
                            selectedFeedUrl = state.value.selectedFeedUrl,
                            onFeedClick = { feed ->
                                coroutineScope.launch { listState.scrollToItem(0) }
                                store.dispatch(FeedAction.SelectFeed(feed))
                            },
                            onFilterClick = {
                                if (state.value.currentFilter.isEmpty()) {
                                    showAddDialog.value = true
                                } else {
                                    store.dispatch(FeedAction.FilterFeed(""))
                                }
                                showAddDialog.value = state.value.currentFilter.isEmpty()
                            },
                            currentFilter = state.value.currentFilter,
                            onEditClick = { modo.forward(Screens.FeedList()) }
                        )
                        Spacer(
                            Modifier
                                .navigationBarsHeight()
                                .fillMaxWidth()
                        )
                    }
                }

                if (showAddDialog.value) {
                    FilterDialog(
                        onEntered = {
                            store.dispatch(FeedAction.FilterFeed(it))
                            coroutineScope.launch { listState.scrollToItem(0) }
                            showAddDialog.value = false
                        },
                        onDismiss = {
                            showAddDialog.value = false
                        }
                    )
                }
            }
        }
    }
}
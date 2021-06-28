package com.github.jetbrains.rssreader.androidApp.ui.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.jetbrains.rssreader.androidApp.ui.compose.icons.EditIcon
import com.github.jetbrains.rssreader.androidApp.ui.compose.icons.FeedIcon
import com.github.jetbrains.rssreader.androidApp.ui.compose.icons.FilterIcon
import com.github.jetbrains.rssreader.core.entity.Feed

private sealed class Icons {
    object All : Icons()
    object Filter : Icons()
    class FeedIcon(val feed: Feed) : Icons()
    object Edit : Icons()
}

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun MainFeedBottomBar(
    feeds: List<Feed>,
    selectedFeedUrl: String?,
    onFeedClick: (Feed?) -> Unit,
    onFilterClick: () -> Unit,
    currentFilter: String,
    onEditClick: () -> Unit
) {
    val items = buildList {
        add(Icons.All)
        add(Icons.Filter)
        addAll(feeds.map { Icons.FeedIcon(it) })
        add(Icons.Edit)
    }
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        this.items(items) { item ->
            when (item) {
                is Icons.All -> FeedIcon(
                    feed = null,
                    isSelected = selectedFeedUrl == null,
                    onClick = { onFeedClick(null) }
                )
                is Icons.Filter -> FilterIcon(
                    isSelected = currentFilter.isNotEmpty(),
                    onClick = onFilterClick
                )
                is Icons.FeedIcon -> FeedIcon(
                    feed = item.feed,
                    isSelected = selectedFeedUrl == item.feed.sourceUrl,
                    onClick = { onFeedClick(item.feed) }
                )
                is Icons.Edit -> EditIcon(onClick = onEditClick)
            }
            Spacer(modifier = Modifier.size(16.dp))
        }
    }
}
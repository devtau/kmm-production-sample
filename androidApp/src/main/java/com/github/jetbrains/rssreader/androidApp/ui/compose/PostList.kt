package com.github.jetbrains.rssreader.androidApp.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.jetbrains.rssreader.androidApp.R.drawable
import com.github.jetbrains.rssreader.core.entity.Post
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.statusBarsHeight
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PostList(
    modifier: Modifier,
    posts: List<Post>,
    listState: LazyListState,
    onClick: (Post) -> Unit
) {
    Box(modifier) {
        /*
            currently cards are not filled if they are not laid out on LazyColumn created
            user sees blank cards if he or she scrolls list up or down
            applying Column + verticalScroll(rememberScrollState()) solves the problem
            therefore we should seek a solution in the onBindViewHolder analog
        */
        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            state = listState
        ) {
            itemsIndexed(posts) { i, post ->
                if (i == 0) Spacer(Modifier.statusBarsHeight())
                PostItem(post) { onClick(post) }
                if (i != posts.size - 1) Spacer(modifier = Modifier.size(16.dp))
            }
        }

        //below is just a bonus improvement not specified in task
        val showButton = listState.firstVisibleItemIndex > 0
        val scope = rememberCoroutineScope()
        AnimatedVisibility(
            visible = showButton,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                onClick = {
                    scope.launch {
                        listState.scrollToItem(0)
                    }
                }
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(drawable.ic_arrow_up),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSecondary),
                    contentDescription = null
                )
            }
        }
    }
}

private val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

@Composable
private fun PostItem(
    item: Post,
    onClick: () -> Unit
) {
    val padding = 16.dp
    Box {
        Card(
            //resolves the current problem with items not being filled on list scroll
            modifier = Modifier.clickable(onClick = onClick),
            elevation = 16.dp,
            shape = RoundedCornerShape(padding)
        ) {
            Column(
                modifier = Modifier.clickable(onClick = onClick)
            ) {
                Spacer(modifier = Modifier.size(padding))
                Text(
                    modifier = Modifier.padding(start = padding, end = padding),
                    style = MaterialTheme.typography.h6,
                    text = item.title
                )
                item.imageUrl?.let { url ->
                    Spacer(modifier = Modifier.size(padding))
                    Image(
                        modifier = Modifier.height(180.dp).fillMaxWidth(),
                        contentScale = ContentScale.FillWidth,
                        painter = rememberCoilPainter(url),
                        contentDescription = null
                    )
                }
                item.desc?.let { desc ->
                    Spacer(modifier = Modifier.size(padding))
                    Text(
                        modifier = Modifier.padding(start = padding, end = padding),
                        style = MaterialTheme.typography.body1,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis,
                        text = desc
                    )
                }
                Spacer(modifier = Modifier.size(padding))
                Text(
                    modifier = Modifier.padding(start = padding, end = padding),
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                    text = dateFormatter.format(Date(item.date))
                )
                Spacer(modifier = Modifier.size(padding))
            }
        }
    }
}

@Preview
@Composable
private fun PostPreview() {
    AppTheme {
        PostItem(item = PreviewData.post, onClick = {})
    }
}
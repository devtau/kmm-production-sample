package com.github.jetbrains.rssreader.androidApp.ui.compose.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.jetbrains.rssreader.androidApp.R
import com.github.jetbrains.rssreader.androidApp.ui.compose.AppTheme

@Composable
fun FilterIcon(
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                color = if (isSelected) MaterialTheme.colors.secondary else Color.Transparent
            )
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .align(Alignment.Center)
                .background(color = MaterialTheme.colors.primary)
                .clickable(enabled = onClick != null, onClick = onClick ?: {})
        ) {
            Image(
                modifier = Modifier.align(Alignment.Center),
                imageVector = ImageVector.vectorResource(R.drawable.ic_filter),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSecondary),
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
private fun FilterIconPreview() {
    AppTheme {
        FilterIcon(isSelected = false)
    }
}

@Preview
@Composable
private fun FilterIconSelectedPreview() {
    AppTheme {
        FilterIcon(isSelected = true)
    }
}
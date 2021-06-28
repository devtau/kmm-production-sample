package com.github.jetbrains.rssreader.core.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Feed(
    @SerialName("title") val title: String,
    @SerialName("link") val link: String,
    @SerialName("description") val desc: String,
    @SerialName("imageUrl") val imageUrl: String?,
    @SerialName("posts") var posts: List<Post>,
    @SerialName("sourceUrl") val sourceUrl: String,
    @SerialName("isDefault") val isDefault: Boolean
)

@Serializable
data class Post(
    @SerialName("title") val title: String,
    @SerialName("link") val link: String?,
    @SerialName("description") val desc: String?,
    @SerialName("imageUrl") val imageUrl: String?,
    @SerialName("date") val date: Long
)
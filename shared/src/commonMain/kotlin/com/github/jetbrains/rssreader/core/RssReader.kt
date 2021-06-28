package com.github.jetbrains.rssreader.core

import com.github.jetbrains.rssreader.core.datasource.network.FeedLoader
import com.github.jetbrains.rssreader.core.datasource.storage.FeedStorage
import com.github.jetbrains.rssreader.core.entity.Feed
import io.github.aakira.napier.Napier
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class RssReader internal constructor(
    private val feedLoader: FeedLoader,
    private val feedStorage: FeedStorage,
    private val settings: Settings = Settings(setOf(JETBRAINS_RSS_ENDPOINT, ECHO_MSK_RSS_ENDPOINT))
) {
    @Throws(Exception::class)
    suspend fun getAllFeeds(
        forceUpdate: Boolean = false,
        filter: String = ""
    ): List<Feed> {
        val feeds = arrayListOf<Feed>()
        feedStorage.getAllFeeds().forEach { feeds.add(it.copy()) }

        if (forceUpdate || feeds.isEmpty()) {
            val feedsUrls = if (feeds.isEmpty()) settings.defaultFeedUrls else feeds.map { it.sourceUrl }
            feeds.clear()
            feeds.addAll(feedsUrls.mapAsync { url ->
                val new = feedLoader.getFeed(url, settings.isDefault(url))
                feedStorage.saveFeed(new)
                new
            })
        }
        val beforeFilter = feeds.sumBy { it.posts.size }
        if (filter.isNotEmpty()) {
            feeds.forEach { nextFeed ->
                nextFeed.posts = nextFeed.posts.filter {
                    it.title.contains(filter, ignoreCase = true)
                }
            }
        }
        val afterFilter = feeds.sumBy { it.posts.size }
        Napier.d(tag = "RssReader", message = "filter: $filter, beforeFilter: $beforeFilter, afterFilter: $afterFilter")

        return feeds
    }

    @Throws(Exception::class)
    suspend fun addFeed(url: String) {
        val feed = feedLoader.getFeed(url, settings.isDefault(url))
        feedStorage.saveFeed(feed)
    }

    @Throws(Exception::class)
    suspend fun deleteFeed(url: String) {
        feedStorage.deleteFeed(url)
    }

    private suspend fun <A, B> Iterable<A>.mapAsync(f: suspend (A) -> B): List<B> =
        coroutineScope { map { async { f(it) } }.awaitAll() }

    companion object {
        private const val JETBRAINS_RSS_ENDPOINT = "https://blog.jetbrains.com/kotlin/feed/"
        /*
            speaking of flaws in this app obvious one is that it doesn't include the RSS feed
            of greatest radio station ever by default
        */
        private const val ECHO_MSK_RSS_ENDPOINT = "https://echo.msk.ru/contributors/venediktov/rss-fulltext.xml"
    }
}

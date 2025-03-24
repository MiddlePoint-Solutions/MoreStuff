package io.middlepoint.morestuff.shared.ui.utils

import coil3.ImageLoader
import coil3.fetch.Fetcher
import coil3.request.Options

expect fun getPlatformFetcherFactory(): Fetcher.Factory<Any>

class DefaultPlatformFetcherFactory : Fetcher.Factory<Any> {
    override fun create(data: Any, options: Options, imageLoader: ImageLoader): Fetcher? {
        return null
    }
}


abstract class PlatformFetcher(
    val data: Any,
    val options: Options
): Fetcher
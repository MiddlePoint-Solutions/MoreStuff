package io.middlepoint.morestuff.shared.ui.utils

import coil3.fetch.Fetcher

actual fun getPlatformFetcherFactory(): Fetcher.Factory<Any> = DefaultPlatformFetcherFactory()
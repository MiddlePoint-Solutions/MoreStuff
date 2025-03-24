package io.middlepoint.morestuff.shared.ui.utils

import io.ktor.client.HttpClient

expect class HttpClientProvider() {
    fun createHttpClient(followRedirects: Boolean = true): HttpClient
}

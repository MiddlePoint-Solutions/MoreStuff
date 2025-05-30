package io.middlepoint.morestuff.shared.ui.utils

import io.ktor.client.HttpClient

actual class HttpClientProvider actual constructor() {
  actual fun createHttpClient(followRedirects: Boolean): HttpClient {
    return HttpClient()
  }
}
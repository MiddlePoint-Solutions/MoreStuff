package io.middlepoint.morestuff.shared.ui.utils

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging


actual class HttpClientProvider {
    actual fun createHttpClient(followRedirects: Boolean): HttpClient {
        return HttpClient {
            expectSuccess = true
            this.followRedirects = followRedirects

            install(HttpTimeout) {
                requestTimeoutMillis = 10000
                connectTimeoutMillis = 10000
            }

            install(UserAgent) {
                agent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36"
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        co.touchlab.kermit.Logger.withTag("CoilHttp").d { message }
                    }
                }
                level = LogLevel.INFO
            }
        }
    }
}
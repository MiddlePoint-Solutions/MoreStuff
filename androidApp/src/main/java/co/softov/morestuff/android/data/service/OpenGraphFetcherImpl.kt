package co.softov.morestuff.android.data.service

import co.softov.morestuff.android.domain.model.OpenGraphResult
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import co.softov.morestuff.android.domain.service.OpenGraphFetcher
import java.util.Locale


class OpenGraphFetcherImpl : OpenGraphFetcher {
    override suspend fun fetchOpenGraphMetadata(inputUrl: String): OpenGraphResult? =
        withContext(Dispatchers.IO) {
            val client = HttpClient(CIO) {
                expectSuccess = true
                followRedirects = true
            }
            try {
                var url = inputUrl
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://$url"
                }

                val response: HttpResponse = client.get(url) {
                    headers {
                        append(HttpHeaders.UserAgent, "WhatsApp/2")
                        append(HttpHeaders.Referrer, "http://www.google.com")
                    }
                }

                val html = response.bodyAsText()

                var openGraphResult = OpenGraphResult()
                val handler = KsoupHtmlHandler
                    .Builder()
                    .onOpenTag { name, attributes, _ ->
                        if (name.lowercase(Locale.getDefault()) == "meta" && attributes["property"]?.lowercase(
                                Locale.getDefault()
                            )
                                ?.startsWith("og:") == true) {
                            when (attributes["property"]?.lowercase(Locale.getDefault())) {
                                "og:image" -> openGraphResult = openGraphResult.copy(image = attributes["content"])
                                "og:description" -> openGraphResult = openGraphResult.copy(description = attributes["content"])
                                "og:url" -> openGraphResult = openGraphResult.copy(url = attributes["content"])
                                "og:title" -> openGraphResult = openGraphResult.copy(title = attributes["content"])
                                "og:site_name" -> openGraphResult = openGraphResult.copy(siteName = attributes["content"])
                                "og:type" -> openGraphResult = openGraphResult.copy(type = attributes["content"])
                            }
                        }
                    }
                    .build()
                val ksoupHtmlParser = KsoupHtmlParser(handler)
                ksoupHtmlParser.write(html)
                ksoupHtmlParser.end()

                return@withContext openGraphResult
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            } finally {
                client.close()
            }
        }
}

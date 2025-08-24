package io.middlepoint.morestuff.shared.data.service

//import io.ktor.client.engine.cio.CIO
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.middlepoint.morestuff.shared.domain.model.OpenGraphResult
import io.middlepoint.morestuff.shared.domain.service.OpenGraphFetcher
import io.middlepoint.morestuff.shared.ui.utils.HttpClientProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class OpenGraphFetcherImpl : OpenGraphFetcher {
  private val httpClientProvider = HttpClientProvider()

  override suspend fun fetchOpenGraphMetadata(inputUrl: String): OpenGraphResult? =
    withContext(Dispatchers.Default) {
      val client = httpClientProvider.createHttpClient(followRedirects = true)

      try {
        var url = inputUrl
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
          url = "https://$url"
        }

        val domainBase = extractDomain(url)
        val referrer =
          if (domainBase.isNotEmpty()) "https://$domainBase" else "https://www.google.com"

        val response: HttpResponse = client.get(url) {
          headers {
            append(
              HttpHeaders.UserAgent,
              "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36"
            )
            append(HttpHeaders.Referrer, referrer)
            append(HttpHeaders.AcceptLanguage, "en-US,en;q=0.9")
            append(
              HttpHeaders.Accept,
              "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8"
            )
          }
        }

        val html = response.bodyAsText()

        var openGraphResult = OpenGraphResult()
        val handler = KsoupHtmlHandler
          .Builder()
          .onOpenTag { name, attributes, _ ->
            if (name.lowercase() == "meta" && attributes["property"]?.lowercase()
                ?.startsWith("og:") == true
            ) {
              when (attributes["property"]?.lowercase()) {
                "og:image" -> openGraphResult = openGraphResult.copy(image = attributes["content"])
                "og:description" -> openGraphResult =
                  openGraphResult.copy(description = attributes["content"])

                "og:url" -> openGraphResult = openGraphResult.copy(url = attributes["content"])
                "og:title" -> openGraphResult = openGraphResult.copy(title = attributes["content"])
                "og:site_name" -> openGraphResult =
                  openGraphResult.copy(siteName = attributes["content"])

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

  private fun extractDomain(url: String): String {
    return try {
      val withoutProtocol = url.replace(Regex("^https?://"), "")
      withoutProtocol.split("/").firstOrNull() ?: ""
    } catch (e: Exception) {
      ""
    }
  }
}

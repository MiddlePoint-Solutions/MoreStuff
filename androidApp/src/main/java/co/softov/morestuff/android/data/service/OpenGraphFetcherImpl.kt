package co.softov.morestuff.android.data.service

import co.softov.morestuff.android.domain.model.OpenGraphResult
import co.softov.morestuff.android.domain.service.OpenGraphFetcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class OpenGraphFetcherImpl : OpenGraphFetcher {

    override suspend fun fetchOpenGraphMetadata(inputUrl: String): OpenGraphResult? =
        withContext(Dispatchers.IO) {
            try {
                val userAgent = "Mozilla"
                val referrer = "http://www.google.com"
                val timeout = 10000
                val docSelectQuery = "meta[property^=og:]"
                val openGraphKey = "content"
                val property = "property"
                val ogImage = "og:image"
                val ogDescription = "og:description"
                val ogUrl = "og:url"
                val ogTitle = "og:title"
                val ogSiteName = "og:site_name"
                val ogType = "og:type"
                var url = inputUrl

                if (!url.contains("http")) {
                    url = "http://$url"
                }

                val response = Jsoup.connect(url).ignoreContentType(true).userAgent(userAgent)
                    .referrer(referrer).timeout(timeout).followRedirects(true).execute()

                val doc = response.parse()

                val ogTags = doc.select(docSelectQuery)

                var openGraphResult = OpenGraphResult()

                ogTags.forEach { tag ->
                    openGraphResult = when (tag.attr(property)) {
                        ogImage -> openGraphResult.copy(image = tag.attr(openGraphKey))
                        ogDescription -> openGraphResult.copy(description = tag.attr(openGraphKey))
                        ogUrl -> openGraphResult.copy(url = tag.attr(openGraphKey))
                        ogTitle -> openGraphResult.copy(title = tag.attr(openGraphKey))
                        ogSiteName -> openGraphResult.copy(siteName = tag.attr(openGraphKey))
                        ogType -> openGraphResult.copy(type = tag.attr(openGraphKey))
                        else -> openGraphResult
                    }
                }

                return@withContext openGraphResult
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }
}
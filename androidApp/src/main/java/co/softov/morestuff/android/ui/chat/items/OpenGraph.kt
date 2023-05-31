package co.softov.morestuff.android.ui.chat.items

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

data class OpenGraphResult(  //CONVERTIR A JSON
    var title: String? = null,
    var description: String? = null,
    var url: String? = null,
    var image: String? = null,
    var siteName: String? = null,
    var type: String? = null
)

suspend fun fetchOpenGraphMetadata(inputUrl: String): OpenGraphResult? = withContext(Dispatchers.IO) {
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

        val response = Jsoup.connect(url)
            .ignoreContentType(true)
            .userAgent(userAgent)
            .referrer(referrer)
            .timeout(timeout)
            .followRedirects(true)
            .execute()

        val doc = response.parse()

        val ogTags = doc.select(docSelectQuery)

        val openGraphResult = OpenGraphResult()

        ogTags.forEach { tag ->

            when (tag.attr(property)) {
                ogImage -> {
                    openGraphResult.image = tag.attr(openGraphKey)
                }
                ogDescription -> {
                    openGraphResult.description = tag.attr(openGraphKey)
                }
                ogUrl -> {
                    openGraphResult.url = tag.attr(openGraphKey)
                }
                ogTitle -> {
                    openGraphResult.title = tag.attr(openGraphKey)
                }
                ogSiteName -> {
                    openGraphResult.siteName = tag.attr(openGraphKey)
                }
                ogType -> {
                    openGraphResult.type = tag.attr(openGraphKey)
                }
            }
        }

        return@withContext openGraphResult
    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext null
    }
}
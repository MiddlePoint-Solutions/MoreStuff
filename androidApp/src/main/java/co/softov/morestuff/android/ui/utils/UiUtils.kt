package co.softov.morestuff.android.ui.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import java.util.regex.Pattern

const val URL_PATTERN_REGEX =
    "(https?://|www\\.|[a-zA-Z0-9_-]+\\.)?[a-zA-Z0-9_-]+(\\.[a-zA-Z]+)+([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?"
val urlPattern: Pattern = Pattern.compile(
    URL_PATTERN_REGEX,
    Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL
)


fun AnnotatedString.Builder.appendUrlsWithStyle(content: String, urlPattern: Pattern, urlColor: Color) {
    val matcher = urlPattern.matcher(content)
    var lastEnd = 0

    while (matcher.find()) {
        val start = matcher.start()
        val end = matcher.end()

        if (start > lastEnd) {
            append(content.substring(lastEnd, start))
        }
        val originalUrl = content.substring(start, end)
        var processedUrl = originalUrl
        if (!processedUrl.startsWith("http://") && !processedUrl.startsWith("https://")) {
            processedUrl = "https://$processedUrl"
        }
        pushStringAnnotation("URL", processedUrl)
        withStyle(
            style = SpanStyle(
                textDecoration = TextDecoration.Underline,
                color = urlColor,
                fontSize = 16.sp
            )
        ) {
            append(originalUrl)
        }
        pop()
        lastEnd = end
    }
    if (lastEnd < content.length) {
        append(content.substring(lastEnd))
    }
}
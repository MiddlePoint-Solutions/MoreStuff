package io.middlepoint.morestuff.shared.ui.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

const val URL_PATTERN_REGEX =
    "(https?://|www\\.|[a-zA-Z0-9_-]+\\.)?[a-zA-Z0-9_-]+(\\.[a-zA-Z]+)+([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?"

val urlPattern: Regex = Regex(
    URL_PATTERN_REGEX, setOf(RegexOption.IGNORE_CASE, RegexOption.IGNORE_CASE)
)

fun AnnotatedString.Builder.appendUrlsWithStyle(
    content: String,
    urlPattern: Regex,
    urlColor: Color
) {
    val matcher = urlPattern.findAll(content)
    var lastEnd = 0

    for (match in matcher) {
        val start = match.range.first
        val end = match.range.last + 1

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

/**
 * Checks if a String contains Emojis
 *
 * @param str is the parameter String to check
 * @return True if String contains any Emojis, otherwise false
 */
fun containsEmoji(str: String): Boolean {
    val emojiRegex = Regex("[\\p{So}\\p{Cn}]")
    return emojiRegex.containsMatchIn(str)
}
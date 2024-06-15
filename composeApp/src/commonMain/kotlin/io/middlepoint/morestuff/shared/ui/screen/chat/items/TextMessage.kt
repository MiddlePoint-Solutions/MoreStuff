package io.middlepoint.morestuff.shared.ui.screen.chat.items

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import io.middlepoint.morestuff.shared.ui.utils.appendUrlsWithStyle
import io.middlepoint.morestuff.shared.ui.utils.urlPattern
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import kotlinx.coroutines.launch

@Composable
private fun OpenGraphContent(message: MessageUiModel) {
    message.openGraphResult?.let { openGraphResult ->
        if (openGraphResult.title != null && openGraphResult.description != null) {
            OpenGraphView(openGraphResult)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageText(
    message: MessageUiModel,
    modifier: Modifier = Modifier,
    showMenu: () -> Unit,
) {
    val (content, url) = handleUrlText(message.content)
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    Surface(
        modifier = modifier.combinedClickable(
            onClick = {
                url?.let {
                    scope.launch { uriHandler.openUri(it) }
                } ?: showMenu()
            },
            onLongClick = showMenu
        ),
        shape = RoundedCornerShape(
            topStart = 14.dp,
            topEnd = 14.dp,
            bottomEnd = 5.dp,
            bottomStart = 14.dp
        ),
        color = MaterialTheme.colorScheme.primary
    ) {
        Column {
            Text(
                modifier = Modifier.padding(
                    start = 14.dp,
                    end = 15.dp,
                    top = 8.dp,
                    bottom = 3.dp
                ),
                text = content,
                style = MaterialTheme.typography.bodyLarge
            )
            OpenGraphContent(message)
            Row(Modifier.align(Alignment.End)) {
                MessageTime(
                    formattedTimeOnly = message.formattedTimeOnly,
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
private fun handleUrlText(text: String): Pair<AnnotatedString, String?> {
    val content = buildAnnotatedString {
        val urlColor = MaterialTheme.colorScheme.onPrimary
        appendUrlsWithStyle(text, urlPattern, urlColor)
    }

    val url = content.getStringAnnotations("URL", start = 0, end = content.length)
        .firstOrNull()?.item

    return Pair(content, url)
}
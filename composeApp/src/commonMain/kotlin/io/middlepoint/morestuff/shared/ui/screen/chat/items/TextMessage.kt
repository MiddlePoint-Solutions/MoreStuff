package io.middlepoint.morestuff.shared.ui.screen.chat.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import io.middlepoint.morestuff.shared.ui.utils.appendUrlsWithStyle
import io.middlepoint.morestuff.shared.ui.utils.urlPattern
import kotlinx.coroutines.launch

@Composable
private fun OpenGraphContent(message: MessageUiModel) {
  message.openGraphResult?.let { openGraphResult ->
    if (openGraphResult.title != null && openGraphResult.description != null) {
      OpenGraphView(openGraphResult)
    }
  }
}

@Composable
fun TextMessageItem(
  message: MessageUiModel,
  modifier: Modifier = Modifier,
) {
  val content = handleUrlText(message.content)
  val scope = rememberCoroutineScope()
  val uriHandler = LocalUriHandler.current

  Column(modifier) {
    ClickableText(
      text = content,
      modifier = Modifier.padding(
        start = 14.dp,
        end = 15.dp,
        top = 8.dp,
        bottom = 3.dp
      ),
      style = MaterialTheme.typography.bodyLarge,
      onClick = { offset ->
        content.getStringAnnotations(tag = "url", start = offset, end = offset).firstOrNull()
          ?.let { scope.launch { uriHandler.openUri(it.item) } }
      }
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

@Composable
private fun handleUrlText(text: String): AnnotatedString {
  return buildAnnotatedString {
    val urlColor = MaterialTheme.colorScheme.outline
    appendUrlsWithStyle(text, urlPattern, urlColor)
  }
}
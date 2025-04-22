package io.middlepoint.morestuff.shared.ui.screen.chat.items

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import io.middlepoint.morestuff.shared.ui.utils.appendUrlsWithStyle
import io.middlepoint.morestuff.shared.ui.utils.urlPattern

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
  onNonLinkClick: () -> Unit = {},
) {


  val content = handleUrlText(message.content)
  val uriHandler = LocalUriHandler.current

  Column(modifier) {

    val annotatedContent = buildAnnotatedString {
      append(content)
      content.getStringAnnotations("url", 0, content.length).forEach { annotation ->
        addStyle(
          style = SpanStyle(
            color = Color.Blue,
            textDecoration = TextDecoration.Underline
          ),
          start = annotation.start,
          end = annotation.end
        )
        addLink(LinkAnnotation.Url(annotation.item), annotation.start, annotation.end)
      }
    }

    // Hold on to the latest TextLayoutResult so we can convert tap coordinates to an offset.
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    BasicText(
      text = annotatedContent,
      modifier = Modifier
        .padding(start = 14.dp, end = 15.dp, top = 6.dp)
        .pointerInput(Unit) {
          detectTapGestures { tapOffset: Offset ->
            layoutResult?.let { result ->
              // Map the tap position to a character offset.
              val position = result.getOffsetForPosition(tapOffset)
              val linkAnnotation =
                annotatedContent.getLinkAnnotations(position, position).firstOrNull()
              // Check if the tap position is inside a link annotation.
              if (linkAnnotation != null) {
                uriHandler.openUri(linkAnnotation.item.toString())
              } else {
                onNonLinkClick()
              }
            } ?: onNonLinkClick()
          }
        },
      style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
      onTextLayout = { result ->
        layoutResult = result
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
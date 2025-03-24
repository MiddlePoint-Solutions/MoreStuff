package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun AutoResizingText(
  text: String,
  textStyle: TextStyle,
  modifier: Modifier = Modifier,
  color: Color = textStyle.color,
  maxLines: Int = 2,
  minLines: Int = 2,
  minTextSize: TextUnit = 16.sp,
  maxTextSize: TextUnit = 25.sp,
  stepGranularity: TextUnit = 1.sp,
  overflow: TextOverflow = TextOverflow.Ellipsis
) {
  var textSize by remember { mutableStateOf(maxTextSize) }
  var readyToDraw by remember { mutableStateOf(false) }

  Text(
    text = text,
    color = color,
    textAlign = TextAlign.Start,
    style = textStyle.copy(
      fontSize = textSize
    ),
    modifier = modifier.drawWithContent {
      if (readyToDraw) {
        drawContent()
      }
    },
    maxLines = maxLines,
    minLines = minLines,
    overflow = overflow,
    onTextLayout = { textLayoutResult ->
      if (textLayoutResult.didOverflowHeight && textSize > minTextSize) {
        textSize = (textSize.value - stepGranularity.value).sp
      } else {
        readyToDraw = true
      }
    }
  )
}

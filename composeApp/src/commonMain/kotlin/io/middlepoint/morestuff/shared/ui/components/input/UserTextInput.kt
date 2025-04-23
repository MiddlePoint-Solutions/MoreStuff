package io.middlepoint.morestuff.shared.ui.components.input

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.ui.extension.clearFocusOnKeyboardDismiss
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.task_chat_input_hint
import morestuff.composeapp.generated.resources.textfield_desc
import org.jetbrains.compose.resources.stringResource


val LocalBoxWeight = compositionLocalOf { 0.12f }

@Composable
fun UserTextInput(
  value: TextFieldValue,
  onValueChange: (TextFieldValue) -> Unit,
  modifier: Modifier = Modifier,
  actionsContent: @Composable BoxScope.() -> Unit = {},
  leadingContent: @Composable (() -> Unit)? = null,
  backgroundColor: Color = MaterialTheme.colorScheme.background,
  focusRequester: FocusRequester = remember { FocusRequester() },
  startWithFocus: Boolean = false,
  isAIEnabled: Boolean = false,
  inputHint: String = stringResource(Res.string.task_chat_input_hint)
) {

  val a11ylabel = stringResource(Res.string.textfield_desc)
  val boxWeight = LocalBoxWeight.current

  val infiniteTransition = rememberInfiniteTransition(label = "neon")
  val animatedOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 400f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 2000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "neonOffset"
  )
  val neonGradient = Brush.linearGradient(
    colors = listOf(
      Color(0xFFBDC6FF),
      Color.White.copy(alpha = 0.7f),
      Color(0xFFDBCAFF)
    ),
    start = Offset(animatedOffset, 0f),
    end = Offset(animatedOffset + 200f, 100f)
  )

  LaunchedEffect(Unit) {
    if (startWithFocus) {
      focusRequester.requestFocus()
    }
  }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .padding(start = 10.dp, end = 10.dp, bottom = 6.dp)
      .animateContentSize(),
    shape = RoundedCornerShape(42),
    color = backgroundColor,
    border = if (isAIEnabled) BorderStroke(2.dp, neonGradient) else null,
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .defaultMinSize(minHeight = 42.dp)
        .semantics {
          contentDescription = a11ylabel
        },
      horizontalArrangement = Arrangement.End,
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (leadingContent != null) {
        Box(
          modifier = Modifier
            .align(Alignment.CenterVertically)
        ) {
          leadingContent()
        }
      }
      BasicTextField(value = value,
        onValueChange = onValueChange,
        modifier = Modifier
          .clearFocusOnKeyboardDismiss()
          .focusRequester(focusRequester)
          .weight(0.88f)
          .align(Alignment.CenterVertically)
          .padding(start = 4.dp, end = 4.dp),
        keyboardOptions = KeyboardOptions(
          capitalization = KeyboardCapitalization.Sentences,
          keyboardType = KeyboardType.Text
        ),
        maxLines = 4,
        cursorBrush = SolidColor(LocalContentColor.current),
        textStyle = LocalTextStyle.current.copy(
          color = LocalContentColor.current, fontSize = 18.sp
        ),
        decorationBox = { innerTextField ->
          Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.padding(bottom = 6.dp, top = 6.dp)
          ) {
            if (value.text.isEmpty()) {
              Text(
                text = inputHint,
                modifier = Modifier.align(Alignment.CenterStart),
                style = LocalTextStyle.current.copy(
                  color = LocalContentColor.current.copy(alpha = 0.6f),
                  fontSize = 18.sp
                )
              )
            }
            innerTextField()
          }
        })

      Box(
        modifier = Modifier
          .weight(if (value.text.isBlank()) boxWeight else 0.15f)
          .align(Alignment.Bottom)
          .padding(end = 8.dp)
      ) {
        actionsContent()
      }
    }
  }


}


//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark"
//)
//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light"
//)
//@Composable
//private fun Preview() {
//    MoreStuffTheme {
//        UserTextInput(
//            value = TextFieldValue(text = ""),
//            onValueChange = {},
//        )
//    }
//}

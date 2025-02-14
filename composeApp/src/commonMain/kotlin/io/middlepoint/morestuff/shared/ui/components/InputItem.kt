package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.ui.extension.clearFocusOnKeyboardDismiss
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.main_input_hint
import org.jetbrains.compose.resources.stringResource

@Composable
fun InputItem(
  modifier: Modifier = Modifier,
  onDone: (String) -> Unit = {},
) {

  var inputValue by rememberSaveable(
    stateSaver = TextFieldValue.Saver,
    key = "inputValue"
  ) {
    mutableStateOf(TextFieldValue(text = ""))
  }

  val focusRequester = remember { FocusRequester() }

  LaunchedEffect(Unit) {
    focusRequester.requestFocus()
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(80.dp)
      .background(color = MaterialTheme.colorScheme.surfaceContainer),
    contentAlignment = Alignment.CenterStart
  ) {
    Row(
      modifier = modifier
        .fillMaxSize()
        .padding(start = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {

      Box(modifier = Modifier) {
        TaskProfile(inputValue.text)
      }

      Spacer(modifier = Modifier.width(8.dp))

      BasicTextField(
        value = inputValue,
        onValueChange = { inputValue = it },
        modifier = Modifier
          .clearFocusOnKeyboardDismiss()
          .focusRequester(focusRequester)
          .weight(0.88f)
          .align(Alignment.CenterVertically)
          .padding(start = 12.dp, end = 4.dp),
        keyboardOptions = KeyboardOptions(
          capitalization = KeyboardCapitalization.Sentences,
          keyboardType = KeyboardType.Text,
          imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
          onDone = {
            onDone(inputValue.text)
          }
        ),
        maxLines = 2,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        textStyle = LocalTextStyle.current.copy(
          color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp
        ),
        decorationBox = { innerTextField ->
          Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.padding(bottom = 6.dp, top = 6.dp)
          ) {
            if (inputValue.text.isEmpty()) {
              Text(
                text = stringResource(Res.string.main_input_hint),
                modifier = Modifier.align(Alignment.CenterStart),
                style = LocalTextStyle.current.copy(
                  color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                  fontSize = 16.sp
                )
              )
            }
            innerTextField()
          }
        })
    }
  }
}


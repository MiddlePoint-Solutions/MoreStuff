package io.middlepoint.morestuff.shared.ui.components.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.middlepoint.morestuff.shared.ui.theme.MoreStuffTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UserInput(
  modifier: Modifier = Modifier,
  enablePadding: Boolean = true,
  priorityContent: @Composable () -> Unit = {},
  textContent: @Composable () -> Unit = {},
) {

  Column(
    modifier = modifier.then(
      if (enablePadding) {
        Modifier
          .imePadding()
          .navigationBarsPadding()
      } else {
        Modifier
      }
    )
  ) {
    priorityContent()
    textContent()
  }
}

@Preview
@Composable
fun UserInputPreview() {
  MoreStuffTheme {
    UserInput()
  }
}

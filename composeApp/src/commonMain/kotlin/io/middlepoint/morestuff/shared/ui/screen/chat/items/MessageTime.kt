package io.middlepoint.morestuff.shared.ui.screen.chat.items

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MessageTime(
  formattedTimeOnly: String?,
  modifier: Modifier = Modifier,
  textColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
  formattedTimeOnly?.let {
    Text(
      text = it,
      style = MaterialTheme.typography.bodySmall.copy(color = textColor),
      modifier = modifier.padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
    )
  }
}
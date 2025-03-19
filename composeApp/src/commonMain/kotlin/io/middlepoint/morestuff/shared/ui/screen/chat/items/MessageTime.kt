package io.middlepoint.morestuff.shared.ui.screen.chat.items

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MessageTime(
  formattedTimeOnly: String?,
  modifier: Modifier = Modifier,
  textColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
  formattedTimeOnly?.let {
    Text(
      text = it,
      style = MaterialTheme.typography.bodySmall.copy(
        color = textColor,
        fontSize = 10.sp,
        letterSpacing = 0.sp,
        lineHeight = 12.sp
      ),
      modifier = modifier.padding(start = 6.dp, end = 6.dp, bottom = 3.dp)
    )
  }
}
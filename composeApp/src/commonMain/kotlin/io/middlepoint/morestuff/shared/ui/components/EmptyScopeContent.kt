package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.unit.dp
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.cta_lets_go
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmptyScopeContent(showTaskInput: () -> Unit) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(bottom = 180.dp)
      .focusable(false)
      .focusProperties { canFocus = false },
    contentAlignment = Alignment.Center
  ) {
    TextButton(onClick = showTaskInput) {
      Text(
        stringResource(Res.string.cta_lets_go),
        style = MaterialTheme.typography.titleLarge.copy(
          color = MaterialTheme.colorScheme.onSurface
        )
      )
    }
  }
}
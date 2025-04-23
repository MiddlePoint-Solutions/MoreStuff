package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.ui.theme.md_theme_light_errorContainer
import io.middlepoint.morestuff.shared.ui.theme.md_theme_light_onErrorContainer
import io.middlepoint.morestuff.shared.ui.theme.surfaceContainerElevation


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteBottomSheet(
  sheetState: SheetState,
  onDismissRequest: () -> Unit,
  title: String,
  message: String,
  extraInfo: String? = null,
  confirmButtonText: String,
  dismissButtonText: String,
  onConfirm: () -> Unit,
) {
  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surfaceContainerElevation,
  ) {
    Column(
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface, fontSize = 24.sp),
        modifier = Modifier.padding(bottom = 16.dp)
      )

      Text(
        text = message,
        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 24.dp)
      )
      if (extraInfo != null) {
        Text(
          text = extraInfo,
          style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.error, fontSize = 12.sp),
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(bottom = 24.dp)
        )
      }
      Button(
        onClick = onConfirm,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = md_theme_light_errorContainer,
          contentColor = md_theme_light_onErrorContainer
        )
      ) {
        Text(text = confirmButtonText)
      }

      Spacer(modifier = Modifier.height(16.dp))
      Button(
        onClick = onDismissRequest,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.secondaryContainer,
          contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
      ) {
        Text(text = dismissButtonText)
      }


      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
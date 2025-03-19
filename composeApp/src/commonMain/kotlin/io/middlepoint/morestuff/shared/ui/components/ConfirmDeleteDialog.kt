package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.cancel
import morestuff.composeapp.generated.resources.delete
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConfirmDeleteDialog(
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
  title: String,
  text: String
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(text = title)
    },
    text = {
      Text(
        text = text,
        textAlign = TextAlign.Start
      )
    },
    confirmButton = {
      TextButton(onClick = onConfirm) {
        Text(stringResource(Res.string.delete))
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(stringResource(Res.string.cancel))
      }
    }
  )
}
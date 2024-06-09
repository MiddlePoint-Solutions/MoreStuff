package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import morestuff.shared.generated.resources.Res
import morestuff.shared.generated.resources.cancel
import morestuff.shared.generated.resources.confirm_delete
import morestuff.shared.generated.resources.delete
import morestuff.shared.generated.resources.sure_delete_task
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConfirmDeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.confirm_delete)) },
        text = {
            Text(
                text = stringResource(Res.string.sure_delete_task),
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
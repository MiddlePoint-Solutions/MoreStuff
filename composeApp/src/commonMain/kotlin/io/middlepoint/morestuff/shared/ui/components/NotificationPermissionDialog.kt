package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.button_enable
import morestuff.composeapp.generated.resources.button_skip
import morestuff.composeapp.generated.resources.notification_permission_rationale
import org.jetbrains.compose.resources.stringResource



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPermissionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onGrantPermission: () -> Unit,
    onSkip: () -> Unit
) {
    if (showDialog) {
        BasicAlertDialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(Res.string.notification_permission_rationale),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            onGrantPermission()
                            onDismiss()
                        }) {
                            Text(text = stringResource(Res.string.button_enable))
                        }
                        TextButton(onClick = {
                            onSkip()
                            onDismiss()
                        }) {
                            Text(text = stringResource(Res.string.button_skip))
                        }
                    }
                }
            }
        }
    }
}

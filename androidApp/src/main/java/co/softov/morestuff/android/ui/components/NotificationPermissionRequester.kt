package co.softov.morestuff.android.ui.components

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPermissionRequester() {

    // Camera permission state
    val permissionState = rememberPermissionState(
        Manifest.permission.POST_NOTIFICATIONS
    )

    var dismiss by remember {
        mutableStateOf(false)
    }

    when (permissionState.status) {
        PermissionStatus.Granted -> {

        }
        is PermissionStatus.Denied -> {
            if (!dismiss)
                Dialog(onDismissRequest = { dismiss = true }) {
                    Card {
                        Column(modifier = Modifier.padding(8.dp)) {
                            val textToShow = if (permissionState.status.shouldShowRationale) {
                                "Notifications are important for this app. Please grant the permission."
                            } else {
                                "Notifications are required for this feature to be available. " +
                                        "Please grant the permission"
                            }
                            Text(textToShow)
                            Button(onClick = { permissionState.launchPermissionRequest() }) {
                                Text("Request permission")
                            }
                        }
                    }
                }
        }
    }
}
package co.softov.morestuff.android.ui.settings

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSlider
import com.alorma.compose.settings.ui.SettingsSwitch
import com.arkivanov.decompose.router.stack.replaceAll
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun DevSettings(
    devTools: DevTools = koinInject(),
) {

    val scope = rememberCoroutineScope()

    var exportData by remember { mutableStateOf(false) }
    if (exportData) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_TITLE, "morestuff.db")
        }
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.also { uri ->
                    scope.launch {
                        devTools.exportData(uri)
                    }
                }
            }
            exportData = false
        }

        LaunchedEffect(Unit) {
            launcher.launch(intent)
        }
    }

    var importData by remember { mutableStateOf(false) }
    if (importData) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/octet-stream"
        }
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.also { uri ->
                    scope.launch {
                        devTools.importData(uri)
                    }
                }
            }
            importData = false
        }

        LaunchedEffect(Unit) {
            launcher.launch(intent)
        }
    }


    val navigation = LocalAppNavigation.current
    SettingsGroup(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.DeveloperBoard, contentDescription = "")
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = "Developer Settings",
                    style = TextStyle(
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                )
            }
        }
    ) {

        SettingsMenuLink(
            title = { Text(text = "Test OnBoarding") },
            onClick = { navigation.replaceAll(Screen.OnBoarding) },
        )

        SettingsMenuLink(
            title = { Text(text = "Test Review Notification") },
            onClick = devTools::testReviewNotification,
        )

        SettingsMenuLink(
            title = { Text(text = "Export Database") },
            onClick = { exportData = true },
        )

        SettingsMenuLink(
            title = { Text(text = "Import Database") },
            onClick = { importData = true },
        )

        DebugMessageSwitch(
            state = rememberAppSettingState(
                defaultValue = { devTools.showDebugMessages },
                valueChanged = { devTools.showDebugMessages = it },
            )
        )
    }

}

@Composable
private fun DebugMessageSwitch(
    state: AppSettingValueState<Boolean>,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsSwitch(
            state = state,
            icon = {
                Icon(
                    imageVector = Icons.Default.Message,
                    contentDescription = "Debug Messages"
                )
            },
            title = {
                Text(
                    text = "Debug Messages",
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Left
                )
            },
            modifier = Modifier.padding(end = 16.dp),
        )
    }
}

@Composable
private fun DebugRemindersSwitch(
    enableState: AppSettingValueState<Boolean>,
    timeState: AppSettingValueState<Float>,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(color = Color(0xff2B3438)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingsSwitch(
                state = enableState,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = ""
                    )
                },
                title = {
                    Text(
                        text = "Debug Reminders",
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Left
                    )
                },
                modifier = Modifier.padding(end = 16.dp),
            )
        }

        SettingsSlider(
            state = timeState,
            title = {
                Text(
                    text = "Reminder delay ${timeState.value.toInt()}",
                )
            },
            steps = 60,
            valueRange = 1F..60F,
            modifier = Modifier.weight(1f),
        )
    }
}
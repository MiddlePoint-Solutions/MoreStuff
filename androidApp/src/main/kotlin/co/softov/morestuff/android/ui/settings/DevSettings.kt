package co.softov.morestuff.android.ui.settings

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.ui.components.SettingsTopBar
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSlider
import com.alorma.compose.settings.ui.SettingsSwitch
import com.arkivanov.decompose.router.stack.replaceAll
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun DevSettingsScreen(
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            SettingsTopBar(
                onBack = onBack,
                title = stringResource(id = R.string.developer_settings)
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            DevSettings()
        }
    }
}

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
    Column {
        SettingsMenuLink(
            title = { Text(text = stringResource(id = R.string.test_onboarding)) },
            onClick = { navigation.replaceAll(Screen.OnBoarding) },
        )

        SettingsMenuLink(
            title = { Text(text = stringResource(id = R.string.test_review_notifications)) },
            onClick = devTools::testReviewNotification,
        )

        SettingsMenuLink(
            title = { Text(text = stringResource(id = R.string.export_database)) },
            onClick = { exportData = true },
        )

        SettingsMenuLink(
            title = { Text(text = stringResource(id = R.string.import_database)) },
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
                    text = stringResource(id = R.string.debug_messages),
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
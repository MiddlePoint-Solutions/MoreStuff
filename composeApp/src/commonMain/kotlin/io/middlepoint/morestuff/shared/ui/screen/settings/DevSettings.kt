package io.middlepoint.morestuff.shared.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSlider
import com.alorma.compose.settings.ui.SettingsSwitch
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import io.middlepoint.morestuff.shared.domain.DevTools
import io.middlepoint.morestuff.shared.domain.nav.Screen
import io.middlepoint.morestuff.shared.ui.components.AppSettingValueState
import io.middlepoint.morestuff.shared.ui.components.SettingsTopBar
import io.middlepoint.morestuff.shared.ui.components.rememberAppSettingState
import io.middlepoint.morestuff.shared.ui.local.LocalAppRouter
import kotlinx.coroutines.launch
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.debug_messages
import morestuff.composeapp.generated.resources.developer_settings
import morestuff.composeapp.generated.resources.disable_developer_settings
import morestuff.composeapp.generated.resources.test_onboarding
import morestuff.composeapp.generated.resources.test_review_notifications
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun DevSettingsScreen(
    onBack: () -> Unit,
    onDevSettingsDisabled: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            SettingsTopBar(
                onBack = onBack,
                title = stringResource(Res.string.developer_settings)
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            DevSettings(onBack = onBack, onDevSettingsDisabled = onDevSettingsDisabled)
        }
    }
}

@Composable
fun DevSettings(
    devTools: DevTools = koinInject(),
    onBack: () -> Unit,
    onDevSettingsDisabled: () -> Unit = {}
) {

    val scope = rememberCoroutineScope()

    // TODO: Import / export database
//    var exportData by remember { mutableStateOf(false) }
//    if (exportData) {
//        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "application/octet-stream"
//            putExtra(Intent.EXTRA_TITLE, "morestuff.db")
//        }
//        val launcher = rememberLauncherForActivityResult(
//            contract = ActivityResultContracts.StartActivityForResult()
//        ) { result: ActivityResult ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                result.data?.data?.also { uri ->
//                    scope.launch {
//                        devTools.exportData(uri.toString())
//                    }
//                }
//            }
//            exportData = false
//        }
//
//        LaunchedEffect(Unit) {
//            launcher.launch(intent)
//        }
//    }
//
//    var importData by remember { mutableStateOf(false) }
//    if (importData) {
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "application/octet-stream"
//        }
//        val launcher = rememberLauncherForActivityResult(
//            contract = ActivityResultContracts.StartActivityForResult()
//        ) { result: ActivityResult ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                result.data?.data?.also { uri ->
//                    scope.launch {
//                        devTools.importData(uri.toString())
//                    }
//                }
//            }
//            importData = false
//        }
//
//        LaunchedEffect(Unit) {
//            launcher.launch(intent)
//        }
//    }


    val navigation = LocalAppRouter.current
    Column {
        DisableDeveloperSettings(
            state = rememberAppSettingState(
                defaultValue = { devTools.showDevSettings },
                valueChanged = { newValue ->
                    devTools.showDevSettings = newValue
                    if (!newValue) {
                        onDevSettingsDisabled()
                        onBack()
                    }
                },
            )
        )
        SettingsMenuLink(
            title = { Text(text = stringResource(Res.string.test_onboarding)) },
            onClick = { navigation.replaceAll(Screen.OnBoarding) },
        )

        SettingsMenuLink(
            title = { Text(text = stringResource(Res.string.test_review_notifications)) },
            onClick = devTools::testReviewNotification,
        )
        SettingsMenuLink(
            title = { Text(text = "Review Screen") },
            onClick = { navigation.push(Screen.Review(1)) },
        )

        SettingsMenuLink(
            title = { Text(text = "Export JSON data") },
            onClick = { scope.launch { devTools.exportJsonData() } },
        )

//        SettingsMenuLink(
//            title = { Text(text = stringResource(Res.string.export_database)) },
//            onClick = { exportData = true },
//        )
//
//        SettingsMenuLink(
//            title = { Text(text = stringResource(Res.string.import_database)) },
//            onClick = { importData = true },
//        )

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
            state = state.value,
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Message,
                    contentDescription = "Debug Messages"
                )
            },
            title = {
                Text(
                    text = stringResource(Res.string.debug_messages),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Left
                )
            },
            modifier = Modifier.padding(end = 16.dp),
            onCheckedChange = { state.value = it }
        )
    }
}

@Composable
private fun DisableDeveloperSettings(
    state: AppSettingValueState<Boolean>,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsSwitch(
            state = state.value,
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "Debug Messages"
                )
            },
            title = {
                Text(
                    text = stringResource(Res.string.disable_developer_settings),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Left
                )
            },
            modifier = Modifier.padding(end = 16.dp),
            onCheckedChange = { state.value = it }
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
                state = enableState.value,
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
                onCheckedChange = { enableState.value = it }
            )
        }

        SettingsSlider(
            value = timeState.value,
            title = {
                Text(
                    text = "Reminder delay ${timeState.value.toInt()}",
                )
            },
            steps = 60,
            valueRange = 1F..60F,
            modifier = Modifier.weight(1f),
            onValueChange = { timeState.value = it }
        )
    }
}
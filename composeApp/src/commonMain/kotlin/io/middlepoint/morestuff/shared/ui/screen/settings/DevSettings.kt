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
import androidx.compose.material3.ListItemDefaults
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
import co.touchlab.kermit.Logger
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSlider
import com.alorma.compose.settings.ui.SettingsSwitch
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.middlepoint.morestuff.shared.domain.DevTools
import io.middlepoint.morestuff.shared.domain.model.Uuid
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
    },
    containerColor = MaterialTheme.colorScheme.surfaceContainer
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

  val test = PlatformFile("")

  val singleImagePickerLauncher = rememberFilePickerLauncher(
    type = FileKitType.File("json"),
  ) { files ->
    files?.let {
      scope.launch {
        devTools.importJsonData(it).let {
          Logger.d { "Data migration successful!" }
        }
      }
    }
  }

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
      onClick = { navigation.replaceAll(Screen.SignIn) },
      colors = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
      )
    )

    SettingsMenuLink(
      title = { Text(text = stringResource(Res.string.test_review_notifications)) },
      onClick = devTools::testReviewNotification,
      colors = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
      )
    )

    SettingsMenuLink(
      title = { Text(text = "Review Screen") },
      onClick = { navigation.push(Screen.Review(Uuid("test"))) },
      colors = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
      )
    )

    SettingsMenuLink(
      title = { Text(text = "Export JSON data") },
      onClick = { scope.launch { devTools.exportJsonData() } },
      colors = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
      )
    )

    SettingsMenuLink(
      title = { Text(text = "Import JSON data") },
      onClick = { scope.launch { singleImagePickerLauncher.launch() } },
      colors = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
      )
    )

    SettingsMenuLink(
      title = { Text(text = "Test data sync push") },
      onClick = { scope.launch { devTools.testDataPush() } },
    )

    SettingsMenuLink(
      title = { Text(text = "Test data sync pull") },
      onClick = { scope.launch { devTools.testDataPull() } },
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
      .background(
        MaterialTheme.colorScheme.surfaceContainer
      )
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
      onCheckedChange = { state.value = it },
      colors = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
      )
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
      .background(
        MaterialTheme.colorScheme.surfaceContainer
      )
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
      onCheckedChange = { state.value = it },
      colors = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
      )
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
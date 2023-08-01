package co.softov.morestuff.android.ui.settings

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
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSlider
import com.alorma.compose.settings.ui.SettingsSwitch
import com.arkivanov.decompose.router.stack.replaceAll

@Composable
fun DevSettings(
    devTools: DevTools,
    modifier: Modifier = Modifier,
) {

    val navigation = LocalAppNavigation.current

    SettingsGroup(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.DeveloperBoard, contentDescription = "")
                Spacer(modifier = Modifier.width(20.dp))
                Text(text = "Developer Settings")
            }
        },
        modifier = modifier
    ) {

        SettingsMenuLink(
            title = { Text(text = "Test OnBoarding") },
            onClick = { navigation.replaceAll(Screen.OnBoarding) },
        )
        SettingsDivider()

        SettingsMenuLink(
            title = { Text(text = "Test Review Notification") },
            onClick = devTools::testReviewNotification,
        )
        SettingsDivider()

        DebugMessageSwitch(
            state = rememberAppSettingState(
                defaultValue = { devTools.showDebugMessages },
                valueChanged = { devTools.showDebugMessages = it },
            )
        )

        SettingsDivider()

    }
}

@Composable
private fun DebugMessageSwitch(
    state: AppSettingValueState<Boolean>
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(color = Color(0xff2B3438)),
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
    timeState: AppSettingValueState<Float>
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
package co.softov.morestuff.android.ui.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsMenuLink
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

    }
}
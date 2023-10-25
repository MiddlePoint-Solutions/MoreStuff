package co.softov.morestuff.android.ui.onboarding

import android.Manifest
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.utils.requiresNotificationsPermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OnBoardingNotificationPermissionScreen(onNext: () -> Unit) {

    val permissionState = if (requiresNotificationsPermission()) {
        rememberPermissionState(
            Manifest.permission.POST_NOTIFICATIONS
        ) { granted ->
            if (granted) {
                onNext()
            }
        }
    } else {
        error("VERSION.SDK_INT < TIRAMISU")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(R.string.notification),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.padding(15.dp))
            Text(
                text = stringResource(R.string.more_stuff_requires_notification),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )

            Spacer(modifier = Modifier.padding(20.dp))
            Image(
                painter = painterResource(id = R.drawable.notification_permission),
                contentDescription = "Notifications permission image",
            )
        }

        Row(
            Modifier
                .padding(bottom = 56.dp)
                .align(Alignment.CenterHorizontally)

        ) {
            Column {
                Button(
                    onClick = {
                        permissionState.launchPermissionRequest()
                    },
                    modifier = Modifier
                        .width(187.dp)
                        .height(43.dp),
                    content = {
                        Text(
                            text = stringResource(R.string.button_enable),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(700),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        )
                    }
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Button(
                    onClick = { onNext() },
                    modifier = Modifier
                        .width(187.dp)
                        .height(43.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    content = {
                        Text(
                            text = stringResource(R.string.button_skip),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(700),
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        )
                    }
                )
            }
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light"
)
@Composable
private fun Preview() {
    MoreStuffTheme {
        OnBoardingNotificationPermissionScreen {}
    }
}

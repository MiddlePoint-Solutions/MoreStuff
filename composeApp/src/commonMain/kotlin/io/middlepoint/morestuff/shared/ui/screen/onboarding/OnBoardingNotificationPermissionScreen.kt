package io.middlepoint.morestuff.shared.ui.screen.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import co.touchlab.kermit.Logger
import com.mohamedrejeb.calf.permissions.ExperimentalPermissionsApi
import com.mohamedrejeb.calf.permissions.Permission
import com.mohamedrejeb.calf.permissions.isGranted
import com.mohamedrejeb.calf.permissions.rememberPermissionState
import com.mohamedrejeb.calf.permissions.shouldShowRationale
import kotlinx.coroutines.launch
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.button_enable
import morestuff.composeapp.generated.resources.button_skip
import morestuff.composeapp.generated.resources.cd_notifications_permission_image
import morestuff.composeapp.generated.resources.notification_permission
import morestuff.composeapp.generated.resources.notification_permission_rationale
import morestuff.composeapp.generated.resources.onboarding_notification_permission_subtitle
import morestuff.composeapp.generated.resources.onboarding_notification_permission_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalPermissionsApi::class
)
@Composable
fun OnBoardingNotificationPermissionScreen(
  onNext: () -> Unit
) {

  var showRationaleDialog by remember { mutableStateOf(false) }
  var checkPermission by remember { mutableStateOf(false) }

  val scope = rememberCoroutineScope()
  val permissionState = rememberPermissionState(Permission.Notification)

  LaunchedEffect(permissionState.status, checkPermission) {
    if (checkPermission) {
      Logger.d { "Permission status: ${permissionState.status}$" }
      when {
        permissionState.status.isGranted -> onNext()
        permissionState.status.shouldShowRationale -> {
          showRationaleDialog = true
          checkPermission = false
        }
        else -> permissionState.openAppSettings()
      }
    }
  }

  if (showRationaleDialog) {
    BasicAlertDialog(
      onDismissRequest = { showRationaleDialog = false },
      properties = DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false
      )
    ) {
      Card {
        Text(
          text = stringResource(Res.string.notification_permission_rationale),
          modifier = Modifier.padding(10.dp)
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = {
            scope.launch { permissionState.launchPermissionRequest() }
            showRationaleDialog = false
            checkPermission = true
          }) {
            Text(text = stringResource(Res.string.button_enable))
          }
          TextButton(onClick = {
            scope.launch { onNext() }
            showRationaleDialog = false
          }) {
            Text(text = stringResource(Res.string.button_skip))
          }
        }
      }
    }
  }

  Box(
    modifier = Modifier
      .windowInsetsPadding(WindowInsets.safeContent)
      .fillMaxSize()
  ) {
    ConstraintLayout(
      modifier = Modifier.fillMaxSize(),
    ) {

      val (image, title, subtitle) = createRefs()

      Text(
        text = stringResource(Res.string.onboarding_notification_permission_title),
        modifier = Modifier
          .fillMaxWidth()
          .constrainAs(title) { top.linkTo(parent.top, margin = 40.dp) },
        style = MaterialTheme.typography.headlineMedium.copy(
          fontSize = 40.sp,
          lineHeight = 44.sp,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.Center
        ),
        color = MaterialTheme.colorScheme.primary,
      )
      Text(
        text = stringResource(Res.string.onboarding_notification_permission_subtitle),
        modifier = Modifier
          .fillMaxWidth()
          .constrainAs(subtitle) { top.linkTo(title.bottom, margin = 20.dp) },
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.secondary,
      )

      Image(
        modifier = Modifier.constrainAs(image) { centerTo(parent) },
        painter = painterResource(Res.drawable.notification_permission),
        contentDescription = stringResource(Res.string.cd_notifications_permission_image),
      )
    }

    Column(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 40.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      OnboardingButton(
        onClick = {
          scope.launch { permissionState.launchPermissionRequest() }
          checkPermission = true
        },
        title = stringResource(Res.string.button_enable)
      )

      Spacer(modifier = Modifier.padding(10.dp))

      TextButton(
        onClick = { onNext() },
        modifier = Modifier
          .width(187.dp)
          .height(43.dp),
        content = {
          Text(
            text = stringResource(Res.string.button_skip),
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

//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    name = "Dark"
//)
//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_NO,
//    name = "Light"
//)
//@Composable
//private fun Preview() {
//    MoreStuffTheme {
//        OnBoardingNotificationPermissionScreen {}
//    }
//}

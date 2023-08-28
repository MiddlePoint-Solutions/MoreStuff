package co.softov.morestuff.android.ui.priority

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import co.softov.morestuff.android.R
import timber.log.Timber
import java.util.UUID

@Composable
fun SchedulePermissionRequester() {

    var showDialog by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    val key = UUID.randomUUID().toString()
    var permissionGranted = remember(key) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val granted = alarmManager.canScheduleExactAlarms()
            Timber.d("permissionGranted = $granted")
            granted
        } else {
            true
        }
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            permissionGranted = it.resultCode == Activity.RESULT_OK
            showDialog = false
        }

    when (permissionGranted) {
        true -> {}
        false -> {

            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.AlarmOff,
                    contentDescription = stringResource(R.string.cd_plan_scheduling_requires_your_permission),
                    tint = Color.Red.copy(alpha = 0.4f)
                )
            }

            if (showDialog)
                Dialog(onDismissRequest = { showDialog = false }) {
                    Card {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Scheduling tasks requires the Alarms & reminders permission")
                            Button(onClick = {
                                Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                    data = Uri.parse("package:${context.packageName}")
                                    launcher.launch(this)
                                }
                            }) {
                                Text("Request permission")
                            }
                        }
                    }
                }
        }
    }
}
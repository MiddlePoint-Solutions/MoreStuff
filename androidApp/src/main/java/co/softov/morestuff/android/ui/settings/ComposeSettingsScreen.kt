package co.softov.morestuff.android.ui.settings

import android.app.Activity
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.BuildConfig
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.storage.base.rememberFloatSettingState
import com.alorma.compose.settings.ui.SettingsList
import com.alorma.compose.settings.ui.SettingsSlider
import com.alorma.compose.settings.ui.SettingsSwitch

@Preview
@Composable
fun SettingsScreen() {
    val scrollState = rememberScrollState()
    MoreStuffTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(scrollState)
                .padding(start = 16.dp)
        ) {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Options()
            SelectTheme()
            SelectSnoozeLimit(viewModel = SettingsViewModel())
            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            DeveloperSettings()
            Notification()
            ReviewTest()
            KeepDeviceScreenOn()
            ReminderDebugging()
            SmartReminder(viewModel = SettingsViewModel())
            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Reset()
            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            About()
        }
    }
}


@Composable
fun Options() {
    Row(
        modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.Text(
            text = "Options", fontSize = 23.sp, color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SelectTheme() {
    val enabledState = rememberBooleanSettingState(true)
    val themeOptions = listOf("Light", "Dark", "System")
    MoreStuffTheme {
        Row {
            SettingsList(enabled = enabledState.value, title = {
                androidx.compose.material3.Text(
                    text = "Select Theme",
                    fontSize = 18.sp
                )
            }, items = themeOptions, onItemSelected = { index, _ ->
                when (index) {
                    0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            })
        }
    }
}


@Composable
fun SelectSnoozeLimit(viewModel: SettingsViewModel) {
    val settingSnoozeLimit = rememberFloatSettingState(0f)
    val enabledState = rememberBooleanSettingState(true)
    MoreStuffTheme {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            SettingsSlider(enabled = enabledState.value,
                state = settingSnoozeLimit,
                title = {
                    androidx.compose.material3.Text(
                        text = "Snooze Limit   ${settingSnoozeLimit.value.toInt()}",
                        fontSize = 18.sp
                    )
                },
                steps = 9,
                valueRange = 0F..10F,
                modifier = Modifier.weight(1f),
                onValueChange = { newValue ->
                    viewModel.onSnoozeLimitChanged(newValue.toInt())
                    settingSnoozeLimit.value = newValue
                }
            )
        }
    }
}

@Composable
fun DeveloperSettings() {
    Row(
        modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.Text(
            text = "Developer Settings",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun Notification() {
    //val onClick = { TODO() }
    MoreStuffTheme {
        Row(
            modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Text(
                text = "Notification",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
            //.clickable(onClick = onClick)
        )
    }
}

@Composable
fun ReviewTest() {
    //val onClick = { TODO() }
    MoreStuffTheme {
        Row(
            modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Text(
                text = "Review Test",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
            //.clickable(onClick = onClick)
        )
    }
}


@Composable
fun KeepDeviceScreenOn() {
    val memoryStorage = rememberBooleanSettingState(defaultValue = false)
    val enabledState = rememberBooleanSettingState(true)
    val context = LocalContext.current
    MoreStuffTheme {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            SettingsSwitch(
                enabled = enabledState.value,
                state = memoryStorage,
                modifier = Modifier.padding(end = 16.dp),
                title = {
                    androidx.compose.material3.Text(
                        text = "Keep device screen on",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
                onCheckedChange = { newValue ->
                    when (newValue) {
                        true ->
                            (context as? Activity)?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        false ->
                            (context as? Activity)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                }
            )
        }
    }
}


@Composable
fun ReminderDebugging() {
    val switchState = remember { mutableStateOf(false) }
    val reminderDelayState = remember { mutableStateOf(60f) }
    MoreStuffTheme {
        Column {
            SettingsSwitch(
                enabled = true,
                modifier = Modifier.padding(end = 16.dp),
                title = {
                    androidx.compose.material3.Text(
                        text = "Reminder Debugging",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Left
                    )
                },
                onCheckedChange = { isChecked -> switchState.value = isChecked }
            )

            if (switchState.value) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        androidx.compose.material3.Text(
                            text = "Today reminder delay",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        androidx.compose.material3.Text(
                            text = reminderDelayState.value.toInt().toString(),
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                }
                Slider(
                    value = reminderDelayState.value,
                    onValueChange = { reminderDelayState.value = it },
                    valueRange = 1f..120f,
                    steps = 119,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    colors = androidx.compose.material.SliderDefaults.colors(
                        thumbColor = Color.Black,
                        activeTrackColor = Color.LightGray,
                        inactiveTrackColor = Color.Gray,
                    ),
                    enabled = switchState.value
                )
            }
        }
    }
}

@Composable
fun SmartReminder(viewModel: SettingsViewModel) {
    val memoryStorage = rememberBooleanSettingState(defaultValue = false)
    val enabledState = rememberBooleanSettingState(true)
    MoreStuffTheme {
        Column(
            horizontalAlignment = Alignment.Start,
        ) {
            SettingsSwitch(
                enabled = enabledState.value,
                state = memoryStorage,
                modifier = Modifier.padding(end = 16.dp),
                title = {
                    Text(
                        text = "Smart Reminder",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Left
                    )
                },
                onCheckedChange = { newValue ->
                    viewModel.smartReminderEnabled(newValue)
                    memoryStorage.value = newValue
                },

                )
        }
    }
}

@Composable
fun Reset() {
    //val onClick = { TODO() }
    MoreStuffTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reset",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }

            Box(
                modifier = Modifier.padding(top = 8.dp)
                    .fillMaxWidth()
                //.clickable(onClick = onClick)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start
                ) {

                    Text(
                        text = "Reset All Notifications",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Left

                    )

                    Text(
                        text = "Clear all pending notification messages",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Left

                    )
                }
            }
        }
    }
}


@Composable
fun About() {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "About",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,

                )
        }

        Row(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = "Version",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = " ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 190.dp, end = 16.dp)
            )
        }

        Box(
            modifier = Modifier.padding(top = 16.dp)
                .fillMaxWidth()
            //.clickable(onClick = { TODO() })
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = "Send feedback",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = "Share your thoughts or whatever",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 30.dp)
                )
            }
        }
    }
}
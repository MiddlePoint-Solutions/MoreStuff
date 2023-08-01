package co.softov.morestuff.android.ui.settings

import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.softov.morestuff.android.BuildConfig
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.darkSurface
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsList
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSlider
import com.alorma.compose.settings.ui.SettingsSwitch
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {

    val navigation = LocalAppNavigation.current
    val model by viewModel.model.collectAsStateWithLifecycle()

    val actions by rememberUpdatedState(
        newValue = SettingsActions(
            selectAppTheme = viewModel::selectAppTheme,
            setSnoozeLimit = viewModel::onSnoozeLimitChanged,
            enableConfetti = viewModel::enableConfetti,
        )
    )

    SettingsContent(
        onBack = navigation::pop,
        model = model,
        actions = actions,
        showLibraries = { navigation.push(Screen.AboutLibraries) },
        devTools = {
            if (BuildConfig.DEBUG) {
                DevSettings(
                    devTools = viewModel.devTools,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            }
        }
    )
}

@Composable
private fun SettingsContent(
    onBack: () -> Unit,
    showLibraries: () -> Unit,
    model: SettingsModel,
    actions: SettingsActions,
    devTools: @Composable () -> Unit = {}
) {

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { SettingsTopBar(onBack = onBack) }
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(scrollState)
        ) {

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
            ) {
                SelectTheme(
                    themeSelected = actions.selectAppTheme,
                    defaultValue = { model.appTheme.ordinal }
                )
                SettingsDivider()

                SelectSnoozeLimit(
                    onSnoozeLimitChanged = actions.setSnoozeLimit,
                    defaultValue = { model.snoozeLimit.toFloat() }
                )

                SettingsDivider()

                EnableConfetti(
                    defaultValue = { model.confettiEnabled },
                    valueChanged = actions.enableConfetti,
                )
                SettingsDivider()

            }

            Column(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {

                devTools()

                SettingsDivider()

                About(
                    showLibraries = showLibraries
                )
            }
        }
    }
}

@Composable
fun SettingsDivider() {
    Divider(color = darkSurface, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.settings),
                style = MaterialTheme.typography.titleLarge,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.cd_navigate_back),
                )
            }
        },
    )
}

@Composable
fun SelectTheme(
    themeSelected: (Int) -> Unit,
    defaultValue: () -> Int,
) {

    val resources = LocalContext.current.resources

    val themeOptions = remember {
        AppTheme.values().map { it.displayTitle(resources) }
    }

    val state = rememberAppSettingState(
        defaultValue = defaultValue,
        valueChanged = themeSelected,
    )

    SettingsList(
        state = state,
        title = {
            Text(
                text = stringResource(R.string.select_theme),
                style = MaterialTheme.typography.titleMedium
            )
        },
        items = themeOptions,
        icon = {
            Icon(
                imageVector = Icons.Default.ColorLens,
                contentDescription = stringResource(R.string.cd_select_theme)
            )
        },
        useSelectedValueAsSubtitle = false,
    )
}

private fun AppTheme.displayTitle(res: Resources): String = when (this) {
    AppTheme.System -> res.getString(R.string.theme_system)
    AppTheme.Light -> res.getString(R.string.theme_light)
    AppTheme.Dark -> res.getString(R.string.theme_dark)
}


@Composable
fun SelectSnoozeLimit(
    onSnoozeLimitChanged: (Int) -> Unit,
    defaultValue: () -> Float
) {
    val state = rememberAppSettingState(
        defaultValue = defaultValue,
        valueChanged = { onSnoozeLimitChanged(it.toInt()) }
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        SettingsSlider(
            state = state,
            icon = {
                Icon(
                    imageVector = Icons.Default.Snooze,
                    contentDescription = "Snooze Limit"
                )
            },
            title = {
                Text(
                    text = "Snooze Limit   ${state.value.toInt()}",
                )
            },
            steps = 9,
            valueRange = 0F..10F,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun DeveloperSettings(
    clearPendingMessages: () -> Unit,
    testReviewActivity: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Developer Settings",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        SettingsMenuLink(
            icon = {
                Icon(
                    imageVector = Icons.Default.ClearAll,
                    contentDescription = "Clear All Message replies"
                )
            },
            title = { Text(text = "Clear All Message replies") },
            subtitle = { Text(text = "This will clear all pending message replies") },
            onClick = clearPendingMessages,
        )

        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        SettingsMenuLink(
            icon = {
                Icon(
                    imageVector = Icons.Default.NotificationAdd,
                    contentDescription = "Show priority review notification"
                )
            },
            title = { Text(text = "Test priority review notification") },
            onClick = testReviewActivity,
        )
    }
}

//@Composable
//fun ReminderDebugging(devTools: DevTools) {
//    val switchState =
//        rememberMultiplatformBooleanSettingState(KEY_REMINDER_DEBUGGING_SWITCH_STATE, false)
//    LaunchedEffect(switchState.value) {
//        devTools.debugReminders = switchState.value
//    }
//
//    Column {
//        SettingsSwitch(
//            enabled = true,
//            modifier = Modifier.padding(end = 16.dp),
//            icon = {
//                Icon(
//                    imageVector = Icons.Default.Alarm,
//                    contentDescription = "Reminder Debugging"
//                )
//            },
//            title = {
//                Text(
//
//                    text = "Reminder Debugging",
//                    color = MaterialTheme.colorScheme.onSurface,
//                    textAlign = TextAlign.Left
//                )
//            }, state = switchState, onCheckedChange = { isChecked ->
//                devTools.debugReminders = isChecked
//            })
//        if (switchState.value) {
//
//            val reminderDelayState = rememberFloatAppSettingState(
//                KEY_REMINDER_DEBUGGING_DELAY_STATE,
//                60f
//            )
//            LaunchedEffect(reminderDelayState.value) {
//                devTools.todayDebugTime = reminderDelayState.value.toInt()
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//
//
//                Column {
//                    Text(
//                        text = "Today reminder delay ${reminderDelayState.value.toInt()}",
//                        color = MaterialTheme.colorScheme.onSurface,
//                    )
//                    SettingsSlider(enabled = switchState.value,
//                        state = reminderDelayState,
//                        title = {},
//                        steps = 119,
//                        valueRange = 1F..120F,
//                        modifier = Modifier.weight(1f),
//                        onValueChange = { newValue ->
//                            devTools.todayDebugTime = newValue.toInt()
//                        })
//                }
//            }
//        }
//    }
//}

@Composable
fun EnableConfetti(
    defaultValue: () -> Boolean,
    valueChanged: (Boolean) -> Unit,
) {

    val state = rememberAppSettingState(
        defaultValue = defaultValue,
        valueChanged = valueChanged
    )

    Column(
        horizontalAlignment = Alignment.Start,
    ) {
        SettingsSwitch(
            state = state,
            modifier = Modifier.padding(end = 16.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.Celebration,
                    contentDescription = stringResource(R.string.enable_confetti)
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.enable_confetti),
                    style = MaterialTheme.typography.titleMedium
                )
            },
        )
    }
}

@Composable
fun About(
    modifier: Modifier = Modifier,
    showLibraries: () -> Unit,
) {
    Column(modifier = modifier.padding(16.dp)) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.about_morestuff),
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Row(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = "Version",
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = " ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 190.dp, end = 16.dp)
            )
        }

        Text(
            text = stringResource(R.string.open_source_libraries),
            style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
            modifier = Modifier.clickable(onClick = showLibraries)
        )

        Text(
            text = stringResource(R.string.privacy_policy),
            style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
            modifier = Modifier.clickable(onClick = showLibraries)
        )

        Box(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
            //.clickable(onClick = { TODO() })
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = "Follow us and join our community!",
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
private fun SettingsPreview() {
    MoreStuffTheme {
        SettingsContent(
            onBack = {},
            showLibraries = {},
            model = SettingsModel(),
            actions = SettingsActions()
        )
    }
}

package co.softov.morestuff.android.ui.settings

import android.content.res.Configuration
import android.content.res.Resources
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.softov.morestuff.android.BuildConfig
import co.softov.morestuff.android.R
import co.softov.morestuff.android.data.Constants.DISCORD_INVITE_LINK
import co.softov.morestuff.android.data.Constants.PRIVACY_POLICY_LINK
import co.softov.morestuff.android.data.Constants.TELEGRAM_INVITE_LINK
import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.domain.enums.Language
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import co.softov.morestuff.android.ui.priority.PriorityTimePicker
import co.softov.morestuff.android.ui.theme.MoreStuffSettingTheme
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.surfaceContainer
import com.alorma.compose.settings.ui.SettingsList
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
) {

    val navigation = LocalAppNavigation.current
    val model by viewModel.model.collectAsStateWithLifecycle()

    val actions by rememberUpdatedState(
        newValue = SettingsActions(
            selectAppTheme = viewModel::selectAppTheme,
            setSnoozeLimit = viewModel::onSnoozeLimitChanged,
            enableConfetti = viewModel::enableConfetti,
            enableDevSettings = viewModel::enableDevSettings,
            onTimeSelected = viewModel::setReviewTime,
            inputVoiceLanguage = viewModel::selectLanguage
        )
    )

    SettingsContent(
        onBack = navigation::pop,
        model = model,
        actions = actions,
        showLibraries = { navigation.push(Screen.AboutLibraries) },
    )
}

@Composable
private fun SettingsContent(
    onBack: () -> Unit,
    showLibraries: () -> Unit,
    model: SettingsModel,
    actions: SettingsActions,
) {

    val scrollState = rememberScrollState()

    MoreStuffSettingTheme {
        Scaffold(
            topBar = { SettingsTopBar(onBack = onBack) },
            containerColor = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .verticalScroll(scrollState)
            ) {
                SelectTheme(
                    themeSelected = actions.selectAppTheme,
                    defaultValue = { model.appTheme.ordinal }
                )

                EnableConfetti(
                    defaultValue = { model.confettiEnabled },
                    valueChanged = actions.enableConfetti,
                )

                ReviewTimeSelector(
                    valueChanged = actions.onTimeSelected,
                    defaultValue = model.reviewTime,
                )
                SelectLanguage(
                    languageSelected = actions.inputVoiceLanguage,
                    defaultValue = { model.inputVoiceLanguage.ordinal }
                )

                if (model.devSettings) {
                    DevSettings()
                }

                About(
                    devSettingsEnabled = model.devSettings,
                    enableDevSettings = actions.enableDevSettings,
                    showLibraries = showLibraries
                )
            }

        }

    }
}

@Composable
fun SettingsDivider() {
    Divider(
        color = MaterialTheme.colorScheme.surfaceContainer,
        thickness = 1.dp,
        modifier = Modifier.fillMaxWidth()
    )
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
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
            )
        },
        items = themeOptions,
        icon = {
            Icon(
                imageVector = Icons.Default.ColorLens,
                contentDescription = stringResource(R.string.cd_select_theme)
            )
        },
        closeDialogDelay = 0,
        useSelectedValueAsSubtitle = false,
    )
}

private fun AppTheme.displayTitle(res: Resources): String = when (this) {
    AppTheme.System -> res.getString(R.string.theme_system)
    AppTheme.Light -> res.getString(R.string.theme_light)
    AppTheme.Dark -> res.getString(R.string.theme_dark)
}

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
                )
            },
        )
    }
}

@Composable
fun About(
    modifier: Modifier = Modifier,
    devSettingsEnabled: Boolean,
    enableDevSettings: () -> Unit,
    showLibraries: () -> Unit,
) {

    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var devSettingsCounter by remember { mutableIntStateOf(7) }

    Surface {
        Column(
            modifier = modifier
                .padding(16.dp)
                .padding(bottom = 23.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                SettingsDivider()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.version),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    modifier = Modifier.clickable {
                        val toast: Toast
                        if (!devSettingsEnabled) {
                            if (devSettingsCounter > 1) {
                                devSettingsCounter -= 1
                                toast = Toast.makeText(
                                    context,
                                    context.resources.getString(
                                        R.string.click_s_to_enable_developer_settings,
                                        devSettingsCounter.toString()
                                    ),
                                    Toast.LENGTH_SHORT
                                )
                            } else {
                                enableDevSettings()
                                toast = Toast.makeText(
                                    context,
                                    context.resources.getText(R.string.developer_settings_enabled),
                                    Toast.LENGTH_SHORT
                                )
                            }
                        } else {
                            toast = Toast.makeText(
                                context,
                                context.resources.getText(R.string.dev_settings_already_enabled),
                                Toast.LENGTH_SHORT
                            )
                        }
                        scope.launch {
                            toast.show()
                            delay(1000)
                            toast.cancel()
                        }
                    },
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            ) {
                Text(
                    text = stringResource(R.string.privacy_policy),
                    style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
                    modifier = Modifier.clickable(onClick = { uriHandler.openUri(PRIVACY_POLICY_LINK) })
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.open_source_libraries),
                    style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
                    modifier = Modifier.clickable(onClick = showLibraries)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Join our community & Help shape MoreStuff! ",
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { uriHandler.openUri(DISCORD_INVITE_LINK) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_discord),
                        contentDescription = "Discord Icon",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Discord Server",
                        style = TextStyle(
                            lineHeight = 28.sp,
                            fontWeight = FontWeight(400),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
                Button(
                    onClick = { uriHandler.openUri(TELEGRAM_INVITE_LINK) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    shape = RoundedCornerShape(15.dp),
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_telegram),
                        contentDescription = "Telegram Icon",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Telegram Page",
                        style = TextStyle(
                            lineHeight = 28.sp,
                            fontWeight = FontWeight(400),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ReviewTimeSelector(
    valueChanged: (hour: Int, minute: Int) -> Unit,
    defaultValue: Pair<Int, Int>,
    modifier: Modifier = Modifier,
) {

    val selectedTimeState = rememberAppSettingState(
        defaultValue = { defaultValue },
        valueChanged = { newTime ->
            valueChanged(newTime.first, newTime.second)

        }
    )
    val defaultHour = selectedTimeState.value.first
    val defaultMinute = selectedTimeState.value.second
    var showTimePickerDialog by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = defaultHour,
        initialMinute = defaultMinute
    )

    if (showTimePickerDialog) {
        PriorityTimePicker(
            dismissTimePicker = { showTimePickerDialog = false },
            onTimeChange = {
                valueChanged(timePickerState.hour, timePickerState.minute)
                selectedTimeState.value = Pair(timePickerState.hour, timePickerState.minute)
            },
            state = timePickerState
        )
    }
    SettingsMenuLink(
        title = {
            Column {
                Text(text = "Set Review Time")
                selectedTimeState.value.let {
                    Text(
                        text = "${it.first}:${String.format("%02d", it.second)}",
                        fontSize = 12.sp

                    )
                }
            }
        },
        onClick = { showTimePickerDialog = true },
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_schedule),
                contentDescription = stringResource(R.string.cd_schedule_icon),
            )
        },
    )
}

@Composable
fun SelectLanguage(
    languageSelected: (Int) -> Unit,
    defaultValue: () -> Int,
) {
    val resources = LocalContext.current.resources
    val languageOptions = remember {
        Language.entries.map { it.displayTitle(resources) }
    }


    val state = rememberAppSettingState(
        defaultValue = defaultValue,
        valueChanged = languageSelected,
    )
    val selectedLanguage = if (Language.entries[state.value] == Language.Device) {
        Locale.getDefault().displayLanguage
    } else {
        languageOptions[state.value]
    }

    SettingsList(
        state = state,
        title = {
            Column {
                Text(text = "Select Language")
                Text(
                    text = selectedLanguage,
                    fontSize = 12.sp
                )
            }
        },
        items = languageOptions,
        icon = {
            Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = stringResource(R.string.select_language)
            )
        },
        closeDialogDelay = 0,
        useSelectedValueAsSubtitle = false,
    )
}

private fun Language.displayTitle(res: Resources): String = when (this) {
    Language.Device -> res.getString(R.string.language_device_default)
    Language.English -> res.getString(R.string.language_english)
    Language.Spanish -> res.getString(R.string.language_spanish)
    Language.Hebrew -> res.getString(R.string.language_hebrew)
    Language.Russian -> res.getString(R.string.language_russian)
    Language.Catalan -> res.getString(R.string.language_catalan)
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
private fun SettingsPreviewDark() {
    MoreStuffTheme {
        SettingsContent(
            onBack = {},
            showLibraries = {},
            model = SettingsModel(),
            actions = SettingsActions()
        )
    }
}

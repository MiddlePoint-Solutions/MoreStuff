package co.softov.morestuff.android.ui.settings

import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import co.softov.morestuff.android.ui.theme.MoreStuffSettingTheme
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.surfaceContainer
import com.alorma.compose.settings.ui.SettingsList
import com.alorma.compose.settings.ui.SettingsSlider
import com.alorma.compose.settings.ui.SettingsSwitch
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import org.koin.androidx.compose.koinViewModel

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
    devTools: @Composable () -> Unit = {},
) {

    val scrollState = rememberScrollState()

    MoreStuffSettingTheme {
        Scaffold(
            topBar = { SettingsTopBar(onBack = onBack) },
            containerColor = Color.Transparent
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

                    EnableConfetti(
                        defaultValue = { model.confettiEnabled },
                        valueChanged = actions.enableConfetti,
                    )

                }

                Column(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {

                    devTools()

                    About(
                        showLibraries = showLibraries
                    )
                }
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
    showLibraries: () -> Unit,
) {

    val uriHandler = LocalUriHandler.current

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
                    text = "Version",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
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
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
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


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
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

package io.middlepoint.morestuff.shared.ui.screen.settings

import MoreStuff.composeApp.BuildConfig
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.ModeStandby
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.dokar.sonner.Toaster
import com.dokar.sonner.rememberToasterState
import io.middlepoint.morestuff.shared.formatString
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.key
import io.github.xxfast.decompose.router.stack.RoutedContent
import io.github.xxfast.decompose.router.stack.rememberRouter
import io.middlepoint.morestuff.android.data.Constants.DISCORD_INVITE_LINK
import io.middlepoint.morestuff.android.data.Constants.PRIVACY_POLICY_LINK
import io.middlepoint.morestuff.android.data.Constants.TELEGRAM_INVITE_LINK
import io.middlepoint.morestuff.shared.domain.enums.Language
import io.middlepoint.morestuff.shared.domain.nav.SettingScreen
import io.middlepoint.morestuff.shared.domain.nav.SettingScreen.AboutLibraries
import io.middlepoint.morestuff.shared.domain.nav.SettingScreen.Developer
import io.middlepoint.morestuff.shared.domain.nav.SettingScreen.Root
import io.middlepoint.morestuff.shared.domain.nav.SettingScreen.Scopes
import io.middlepoint.morestuff.shared.ui.components.SettingsTopBar
import io.middlepoint.morestuff.shared.ui.components.priority.PriorityTimePicker
import io.middlepoint.morestuff.shared.ui.components.rememberAppSettingState
import io.middlepoint.morestuff.shared.ui.screen.scopes.ScopesScreen
import io.middlepoint.morestuff.shared.ui.theme.MoreStuffSettingTheme
import kotlinx.coroutines.launch
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.cd_schedule_icon
import morestuff.composeapp.generated.resources.click_s_to_enable_developer_settings
import morestuff.composeapp.generated.resources.dev_settings_already_enabled
import morestuff.composeapp.generated.resources.developer_settings
import morestuff.composeapp.generated.resources.developer_settings_enabled
import morestuff.composeapp.generated.resources.ic_discord
import morestuff.composeapp.generated.resources.ic_schedule
import morestuff.composeapp.generated.resources.ic_telegram
import morestuff.composeapp.generated.resources.join_community
import morestuff.composeapp.generated.resources.language_catalan
import morestuff.composeapp.generated.resources.language_device_default
import morestuff.composeapp.generated.resources.language_english
import morestuff.composeapp.generated.resources.language_hebrew
import morestuff.composeapp.generated.resources.language_russian
import morestuff.composeapp.generated.resources.language_spanish
import morestuff.composeapp.generated.resources.open_source_libraries
import morestuff.composeapp.generated.resources.privacy_policy
import morestuff.composeapp.generated.resources.set_review_time
import morestuff.composeapp.generated.resources.settings
import morestuff.composeapp.generated.resources.title_scopes
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.LocalKoinApplication
import org.koin.core.Koin
import org.koin.core.parameter.ParametersDefinition
import kotlin.reflect.KClass

@Composable
fun <T : Any> koinInjectOnRoute(
    type: KClass<T>,
    key: Any = type.key,
    parameters: ParametersDefinition? = null,
    block: @DisallowComposableCalls ((koin: Koin) -> T)? = null
): T {
    class RouteInstance(val instance: T) : InstanceKeeper.Instance

    val routerContext: RouterContext = LocalRouterContext.current
    val koin: Koin = LocalKoinApplication.current
    val instanceKeeper: InstanceKeeper = routerContext.instanceKeeper
    val routeInstance: RouteInstance = remember(key) {
        instanceKeeper.getOrCreate(key) {
            RouteInstance(
                block?.invoke(koin) ?: koin.get(clazz = type, parameters = parameters)
            )
        }
    }
    return routeInstance.instance
}


@Composable
fun SettingsScreen(
    onBack: () -> Unit,
) {

    val router = rememberRouter(SettingScreen::class) {
        listOf(Root)
    }

    val viewModel = koinInjectOnRoute(SettingsViewModel::class)

    val model by viewModel.models.collectAsState()

    RoutedContent(
        router = router,
        modifier = Modifier.background(Color.Transparent),
        animation = stackAnimation(slide()),
    ) { screen ->

        when (screen) {
            Root -> {
                SettingsContent(
                    onBack = onBack,
                    model = model,
                    selectAppTheme = { index -> viewModel.take(SettingsEvent.SelectAppTheme(index)) },
                    setReviewTime = { hour, minute ->
                        viewModel.take(
                            SettingsEvent.SetReviewTime(
                                hour,
                                minute
                            )
                        )
                    },
                    selectLanguage = { index -> viewModel.take(SettingsEvent.SelectLanguage(index)) },
                    enableDevSettings = { viewModel.take(SettingsEvent.EnableDevSettings) },
                    showDevSettings = { router.push(Developer) },
                    showScopesSettings = { router.push(Scopes) },
                    showLibraries = { router.push(AboutLibraries) },
                )
            }

            Developer -> DevSettingsScreen(onBack = router::pop)

            Scopes -> ScopesScreen(onBack = router::pop)

            AboutLibraries -> AboutLibrariesScreen(onBack = router::pop)
        }
    }
}

@Composable
fun SettingsContent(
    onBack: () -> Unit,
    model: SettingsState,
    selectAppTheme: (Int) -> Unit,
    setReviewTime: (Int, Int) -> Unit,
    selectLanguage: (Int) -> Unit,
    enableDevSettings: () -> Unit,
    showLibraries: () -> Unit,
    showDevSettings: () -> Unit,
    showScopesSettings: () -> Unit
) {

    val scrollState = rememberScrollState()

    MoreStuffSettingTheme {
        Scaffold(
            topBar = {
                SettingsTopBar(
                    onBack = onBack,
                    title = stringResource(Res.string.settings)
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) {

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .verticalScroll(scrollState)
            ) {

                val (settings, about) = createRefs()

                Column(
                    modifier = Modifier.constrainAs(settings) {
                        top.linkTo(parent.top)
                        bottom.linkTo(about.top)
                        height = Dimension.fillToConstraints
                    }
                ) {

                    SelectTheme(
                        themeSelected = selectAppTheme,
                        defaultValue = { model.appTheme.ordinal }
                    )

                    ReviewTimeSelector(
                        valueChanged = setReviewTime,
                        defaultValue = model.reviewTime,
                    )
                    SelectLanguage(
                        languageSelected = selectLanguage,
                        defaultValue = { model.inputVoiceLanguage.ordinal }
                    )

                    ScopeSettings(onClick = showScopesSettings)

                    if (model.devSettings) {
                        SettingsMenuLink(
                            title = {
                                Text(text = stringResource(Res.string.developer_settings))
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.DeveloperBoard,
                                    contentDescription = ""
                                )
                            },
                            onClick = showDevSettings,
                        )
                    }
                }

                About(
                    devSettingsEnabled = model.devSettings,
                    enableDevSettings = enableDevSettings,
                    showLibraries = showLibraries,
                    modifier = Modifier.constrainAs(about) {
                        bottom.linkTo(parent.bottom)
                    }
                )
            }

        }

    }
}

@Composable
private fun SelectTheme(
    themeSelected: (Int) -> Unit,
    defaultValue: () -> Int,
) {

    val state = rememberAppSettingState(
        defaultValue = defaultValue,
        valueChanged = themeSelected,
    )

    // TODO: SettingsList is not available anymore, need to implement the dialog ourselves
//  SettingsList(
//    state = state,
//    title = {
//      Text(
//        text = stringResource(Res.string.select_theme),
//      )
//    },
//    items = themeOptions,
//    icon = {
//      Icon(
//        imageVector = Icons.Default.ColorLens,
//        contentDescription = stringResource(Res.string.cd_select_theme)
//      )
//    },
//    closeDialogDelay = 0,
//    useSelectedValueAsSubtitle = false,
//  )
}


@Composable
private fun About(
    modifier: Modifier = Modifier,
    devSettingsEnabled: Boolean,
    enableDevSettings: () -> Unit,
    showLibraries: () -> Unit,
) {

    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()

    val toaster = rememberToasterState()
    var devSettingsCounter by remember { mutableIntStateOf(7) }

    Surface(modifier = modifier) {
        HorizontalDivider()

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {

            Text(
                text = stringResource(Res.string.join_community),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { uriHandler.openUri(DISCORD_INVITE_LINK) }
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_discord),
                        contentDescription = "Discord Icon",
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = { uriHandler.openUri(TELEGRAM_INVITE_LINK) },
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_telegram),
                        contentDescription = "Telegram Icon",
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            ) {
                Text(
                    text = stringResource(Res.string.privacy_policy),
                    style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
                    modifier = Modifier.clickable(onClick = { uriHandler.openUri(PRIVACY_POLICY_LINK) })
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                Text(
                    text = stringResource(Res.string.open_source_libraries),
                    style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
                    modifier = Modifier.clickable(onClick = showLibraries)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            ) {
                Text(
                    text = "V${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.clickable {
                        scope.launch {
                            if (!devSettingsEnabled) {
                                if (devSettingsCounter > 1) {
                                    val message = getString(
                                        Res.string.click_s_to_enable_developer_settings,
                                        devSettingsCounter.toString()
                                    )
                                    devSettingsCounter -= 1
                                    toaster.show(message, id = "DevSettings")
                                } else {
                                    enableDevSettings()
                                    val message = getString(
                                        Res.string.developer_settings_enabled,
                                        devSettingsCounter.toString()
                                    )
                                    toaster.show(message, id = "DevSettings")
                                }
                            } else {
                                val message = getString(
                                    Res.string.dev_settings_already_enabled,
                                    devSettingsCounter.toString()
                                )
                                toaster.show(message, id = "DevSettings")
                            }
                        }
                    }
                )
            }
        }
    }

    Toaster(
        state = toaster
    )
}

@Composable
private fun showToast() {

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ReviewTimeSelector(
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
                Text(text = stringResource(Res.string.set_review_time))
                selectedTimeState.value.let {
                    Text(
                        text = "${it.first}:${formatString("%02d", it.second)}",
                        fontSize = 12.sp

                    )
                }
            }
        },
        onClick = { showTimePickerDialog = true },
        icon = {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_schedule),
                contentDescription = stringResource(Res.string.cd_schedule_icon),
            )
        },
    )
}

@Composable
private fun SelectLanguage(
    languageSelected: (Int) -> Unit,
    defaultValue: () -> Int,
) {
//  val languageOptions = remember {
//    Language.entries.map { it.displayTitle() }
//  }


    val state = rememberAppSettingState(
        defaultValue = defaultValue,
        valueChanged = languageSelected,
    )
//  val selectedLanguage = if (Language.entries[state.value] == Language.Device) {
//    Locale.current.toLanguageTag()
//  } else {
//    languageOptions[state.value]
//  }

    // TODO:
//  SettingsList(
//    state = state,
//    title = {
//      Column {
//        Text(text = stringResource(Res.string.select_language))
//        Text(
//          text = selectedLanguage,
//          fontSize = 12.sp
//        )
//      }
//    },
//    items = languageOptions,
//    icon = {
//      Icon(
//        imageVector = Icons.Default.Translate,
//        contentDescription = stringResource(Res.string.select_language)
//      )
//    },
//    closeDialogDelay = 0,
//    useSelectedValueAsSubtitle = false,
//  )
}

private suspend fun Language.displayTitle(): String = when (this) {
    Language.Device -> getString(Res.string.language_device_default)
    Language.English -> getString(Res.string.language_english)
    Language.Spanish -> getString(Res.string.language_spanish)
    Language.Hebrew -> getString(Res.string.language_hebrew)
    Language.Russian -> getString(Res.string.language_russian)
    Language.Catalan -> getString(Res.string.language_catalan)
}

@Composable
private fun ScopeSettings(onClick: () -> Unit) {
    SettingsMenuLink(
        title = { Text(text = stringResource(Res.string.title_scopes)) },
        onClick = onClick,
        icon = {
            Icon(
                imageVector = Icons.Default.ModeStandby,
                contentDescription = "Scopes"
            )
        }
    )
}

//@Preview(
//  uiMode = Configuration.UI_MODE_NIGHT_YES,
//  name = "Dark"
//)
//@Preview(
//  uiMode = Configuration.UI_MODE_NIGHT_NO,
//  name = "Light"
//)
//@Composable
//private fun SettingsPreviewDark() {
//  MoreStuffTheme {
//    SettingsContent(
//      onBack = {},
//      showLibraries = {},
//      showDevSettings = {},
//      showScopesSettings = {},
//      model = SettingsState(),
//      enableDevSettings = {},
//      selectAppTheme = {},
//      selectLanguage = {},
//      setReviewTime = { _, _ -> }
//    )
//  }
//}

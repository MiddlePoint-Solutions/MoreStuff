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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.ModeStandby
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.dokar.sonner.Toaster
import com.dokar.sonner.rememberToasterState
import com.mohamedrejeb.calf.permissions.ExperimentalPermissionsApi
import com.mohamedrejeb.calf.permissions.Permission
import com.mohamedrejeb.calf.permissions.isGranted
import com.mohamedrejeb.calf.permissions.rememberPermissionState
import com.mohamedrejeb.calf.permissions.shouldShowRationale
import io.github.xxfast.decompose.router.stack.RoutedContent
import io.github.xxfast.decompose.router.stack.rememberRouter
import io.middlepoint.morestuff.android.data.Constants.DISCORD_INVITE_LINK
import io.middlepoint.morestuff.android.data.Constants.PRIVACY_POLICY_LINK
import io.middlepoint.morestuff.android.data.Constants.REDDIT_INVITE_LINK
import io.middlepoint.morestuff.android.data.Constants.TELEGRAM_INVITE_LINK
import io.middlepoint.morestuff.shared.domain.enums.AppTheme
import io.middlepoint.morestuff.shared.domain.enums.Language
import io.middlepoint.morestuff.shared.domain.nav.SettingScreen
import io.middlepoint.morestuff.shared.domain.nav.SettingScreen.AboutLibraries
import io.middlepoint.morestuff.shared.domain.nav.SettingScreen.Developer
import io.middlepoint.morestuff.shared.domain.nav.SettingScreen.Root
import io.middlepoint.morestuff.shared.domain.nav.SettingScreen.Scopes
import io.middlepoint.morestuff.shared.platform.Platform
import io.middlepoint.morestuff.shared.platform.formatString
import io.middlepoint.morestuff.shared.platform.platform
import io.middlepoint.morestuff.shared.ui.components.SettingsTopBar
import io.middlepoint.morestuff.shared.ui.components.priority.PriorityTimePicker
import io.middlepoint.morestuff.shared.ui.components.rememberAppSettingState
import io.middlepoint.morestuff.shared.ui.screen.scopes.ScopesScreen
import io.middlepoint.morestuff.shared.ui.theme.surfaceContainerElevation
import kotlinx.coroutines.launch
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.api_key_not_set
import morestuff.composeapp.generated.resources.api_key_title
import morestuff.composeapp.generated.resources.button_enable
import morestuff.composeapp.generated.resources.button_skip
import morestuff.composeapp.generated.resources.cd_schedule_icon
import morestuff.composeapp.generated.resources.cd_select_theme
import morestuff.composeapp.generated.resources.click_s_to_enable_developer_settings
import morestuff.composeapp.generated.resources.dev_settings_already_enabled
import morestuff.composeapp.generated.resources.developer_settings
import morestuff.composeapp.generated.resources.developer_settings_enabled
import morestuff.composeapp.generated.resources.ic_discord
import morestuff.composeapp.generated.resources.ic_reddit
import morestuff.composeapp.generated.resources.ic_schedule
import morestuff.composeapp.generated.resources.ic_telegram
import morestuff.composeapp.generated.resources.join_community
import morestuff.composeapp.generated.resources.language_catalan
import morestuff.composeapp.generated.resources.language_device_default
import morestuff.composeapp.generated.resources.language_english
import morestuff.composeapp.generated.resources.language_hebrew
import morestuff.composeapp.generated.resources.language_russian
import morestuff.composeapp.generated.resources.language_spanish
import morestuff.composeapp.generated.resources.notification_permission_already_granted
import morestuff.composeapp.generated.resources.notification_permission_rationale
import morestuff.composeapp.generated.resources.ok
import morestuff.composeapp.generated.resources.onboarding_notification_permission_title
import morestuff.composeapp.generated.resources.open_source_libraries
import morestuff.composeapp.generated.resources.privacy_policy
import morestuff.composeapp.generated.resources.select_language
import morestuff.composeapp.generated.resources.select_theme
import morestuff.composeapp.generated.resources.set_review_time
import morestuff.composeapp.generated.resources.settings
import morestuff.composeapp.generated.resources.title_scopes
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun SettingsScreen(
  onBack: () -> Unit,
) {

  val router = rememberRouter<SettingScreen> { listOf(Root) }
  val viewModel = koinInjectOnRoute(SettingsViewModel::class)

  val model by viewModel.models.collectAsState()

  RoutedContent(
    router = router,
    animation = stackAnimation(slide()),
  ) { screen ->

    when (screen) {
      Root -> {
        SettingsContent(
          // TODO: pass ViewModel::take and a router lambda to reduce the number of properties.
          onBack = onBack,
          model = model,
          selectAppTheme = { index -> viewModel.take(SettingsEvent.SelectAppTheme(index)) },
          selectLanguage = { index -> viewModel.take(SettingsEvent.SelectLanguage(index)) },
          enableDevSettings = { viewModel.take(SettingsEvent.EnableDevSettings(true)) },
          showDevSettings = { router.push(Developer) },
          showScopesSettings = { router.push(Scopes) },
          showLibraries = { router.push(AboutLibraries) },
          signOut = { viewModel.take(SettingsEvent.SignOut) },
          openAppSettings = { viewModel.take(SettingsEvent.OpenAppSettings) },
          setApiKey = { apiKey -> viewModel.take(SettingsEvent.SetApiKey(apiKey)) }
        )
      }

      Developer -> DevSettingsScreen(
        onBack = router::pop,
        onDevSettingsDisabled = { viewModel.take(SettingsEvent.EnableDevSettings(false)) }
      )

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
  selectLanguage: (Int) -> Unit,
  enableDevSettings: () -> Unit,
  signOut: () -> Unit,
  showLibraries: () -> Unit,
  showDevSettings: () -> Unit,
  showScopesSettings: () -> Unit,
  openAppSettings: () -> Unit,
  setApiKey: (String) -> Unit
) {

  val scrollState = rememberScrollState()

  Scaffold(
    topBar = {
      SettingsTopBar(
        onBack = onBack,
        title = stringResource(Res.string.settings)
      )
    },
    containerColor = MaterialTheme.colorScheme.surfaceContainer
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

        if (Platform.iOS == platform) {
          NotificationPermissionButton()
        }


        ScopeSettings(onClick = showScopesSettings)

        if (Platform.Android == platform) {
          LanguageSettings(onClick = openAppSettings)
        }

        /*   ApiKeySettings(
             apiKey = model.apiKey,
             onApiKeyChange = setApiKey
           )*/


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
            colors = ListItemDefaults.colors(
              containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
          )
        }

        SettingsMenuLink(
          title = {
            Text(text = "Sign out")
          },
          icon = {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Logout,
              contentDescription = ""
            )
          },
          onClick = signOut,
          colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
          )
        )
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


@Composable
fun ApiKeySettings(
  apiKey: String,
  onApiKeyChange: (String) -> Unit,
) {
  var showBottomSheet by remember { mutableStateOf(false) }


  if (showBottomSheet) {
    ApiKeyBottomSheet(
      isVisible = true,
      onDismiss = { showBottomSheet = false },
      currentApiKey = apiKey,
      onSave = onApiKeyChange
    )
  }


  SettingsMenuLink(
    title = {
      Text(text = stringResource(Res.string.api_key_title))
    },
    subtitle = {
      Text(
        text = if (apiKey.isNotEmpty()) {
          val visiblePart = apiKey.take(4)
          val hiddenPart = "*".repeat(minOf(apiKey.length - 4, 8))
          "$visiblePart$hiddenPart"
        } else {
          stringResource(Res.string.api_key_not_set)
        },
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    },
    onClick = { showBottomSheet = true },
    icon = {
      Icon(
        imageVector = Icons.Default.Key,
        contentDescription = "API Key"
      )
    },
    colors = ListItemDefaults.colors(
      containerColor = MaterialTheme.colorScheme.surfaceContainer
    )
  )
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

  val themeOptions = remember { AppTheme.entries.map { it.name } }

  SettingsList(
    state = state,
    title = {
      Text(
        text = stringResource(Res.string.select_theme),
        style = MaterialTheme.typography.bodyLarge.copy(
          color = MaterialTheme.colorScheme.onSurface
        ),
      )
    },
    subtitle = {
      Text(
        text = themeOptions[state.value],
      )
    },
    items = themeOptions,
    icon = {
      Icon(
        imageVector = Icons.Default.ColorLens,
        contentDescription = stringResource(Res.string.cd_select_theme)
      )
    },
    closeDialogDelay = 0,

    )
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
  var devSettingsCounter by remember { mutableIntStateOf(6) }
  var initialClickCounter by remember { mutableIntStateOf(0) }
  var showDevCounter by remember { mutableStateOf(false) }

  Surface(modifier = modifier) {
    HorizontalDivider()

    Column(
      modifier = Modifier
        .background(color = MaterialTheme.colorScheme.surfaceContainer)
        .padding(horizontal = 16.dp, vertical = 16.dp)
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
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = { uriHandler.openUri(REDDIT_INVITE_LINK) }
        ) {
          Icon(
            imageVector = vectorResource(Res.drawable.ic_reddit),
            contentDescription = "Reddit Icon",
            modifier = Modifier.size(36.dp),
            tint = MaterialTheme.colorScheme.onSurface
          )
        }

        Spacer(modifier = Modifier.width(24.dp))

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
          // TODO: enable when needed
//          modifier = Modifier
//            .clickable {
//            scope.launch {
//              if (!devSettingsEnabled) {
//                if (!showDevCounter) {
//                  initialClickCounter++
//                  if (initialClickCounter >= 3) {
//                    showDevCounter = true
//                  }
//                } else {
//                  if (devSettingsCounter > 0) {
//                    val message = getString(
//                      Res.string.click_s_to_enable_developer_settings,
//                      devSettingsCounter.toString()
//                    )
//                    devSettingsCounter -= 1
//                    toaster.show(message, id = "DevSettings", duration = 400.milliseconds)
//                  } else {
//                    enableDevSettings()
//                    val message = getString(
//                      Res.string.developer_settings_enabled,
//                      devSettingsCounter.toString()
//                    )
//                    toaster.show(message, id = "DevSettings", duration = 400.milliseconds)
//                    showDevCounter = false
//                    initialClickCounter = 0
//                    devSettingsCounter = 6
//                  }
//                }
//              } else {
//                val message = getString(
//                  Res.string.dev_settings_already_enabled,
//                  devSettingsCounter.toString()
//                )
//                toaster.show(message, id = "DevSettings", duration = 400.milliseconds)
//              }
//            }
//          }
        )
      }
    }
  }

  Toaster(
    state = toaster
  )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ReviewTimeSelector(
  valueChanged: (hour: Int, minute: Int) -> Unit,
  defaultValue: Pair<Int, Int>,
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
      Text(text = stringResource(Res.string.set_review_time))

    },
    subtitle = {
      selectedTimeState.value.let {
        Text(
          text = "${it.first}:${formatString("%02d", it.second)}",
          fontSize = 12.sp

        )
      }
    },
    onClick = { showTimePickerDialog = true },
    icon = {
      Icon(
        imageVector = vectorResource(Res.drawable.ic_schedule),
        contentDescription = stringResource(Res.string.cd_schedule_icon),
        modifier = Modifier.padding(end = 5.dp)
      )
    },
    colors = ListItemDefaults.colors(
      containerColor = MaterialTheme.colorScheme.surfaceContainerElevation
    )
  )
}

@Composable
private fun SelectLanguage(
  languageSelected: (Int) -> Unit,
  defaultValue: () -> Int,
) {
  var languageOptions by remember { mutableStateOf(listOf("")) }
  var selectedLanguage by remember { mutableStateOf("") }
  var currentSelectedIndex by remember { mutableStateOf(defaultValue()) }

  LaunchedEffect(Unit, currentSelectedIndex) {
    languageOptions = Language.entries.map { it.displayTitle() }

    selectedLanguage = if (Language.entries[currentSelectedIndex] == Language.Device) {
      val currentLocale = Locale.current.toLanguageTag()
      localeToStringResource(currentLocale)
    } else {
      Language.entries[currentSelectedIndex].displayTitle()
    }
  }

  val state = rememberAppSettingState(
    defaultValue = defaultValue,
    valueChanged = { newIndex ->
      currentSelectedIndex = newIndex
      languageSelected(newIndex)
    },
  )

  SettingsList(
    state = state,
    title = {
      Column {
        Text(
          text = stringResource(Res.string.select_language),
        )
      }
    },
    subtitle = {
      Text(
        text = selectedLanguage,
        style = MaterialTheme.typography.bodySmall
      )
    },
    items = languageOptions,
    icon = {
      Icon(
        imageVector = Icons.Default.Translate,
        contentDescription = stringResource(Res.string.select_language)
      )
    },
  )
}

private suspend fun localeToStringResource(currentLocale: String) = when {
  currentLocale.startsWith("en") -> getString(Res.string.language_english)
  currentLocale.startsWith("es") -> getString(Res.string.language_spanish)
  currentLocale.startsWith("he") -> getString(Res.string.language_hebrew)
  currentLocale.startsWith("ru") -> getString(Res.string.language_russian)
  currentLocale.startsWith("ca") -> getString(Res.string.language_catalan)
  else -> getString(Res.string.language_device_default)
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
    },
    colors = ListItemDefaults.colors(
      containerColor = MaterialTheme.colorScheme.surfaceContainer
    )
  )
}

@Composable
private fun LanguageSettings(onClick: () -> Unit) {
  SettingsMenuLink(
    title = { Text(text = "Language") },
    onClick = onClick,
    icon = {
      Icon(
        imageVector = Icons.Default.Language,
        contentDescription = "Scopes"
      )
    },
    colors = ListItemDefaults.colors(
      containerColor = MaterialTheme.colorScheme.surfaceContainer
    )
  )
}


@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
fun NotificationPermissionButton() {
  var showExplanationDialog by remember { mutableStateOf(false) }
  var showRationaleDialog by remember { mutableStateOf(false) }
  var showAlreadyGrantedDialog by remember { mutableStateOf(false) }
  var checkPermission by remember { mutableStateOf(false) }
  val scope = rememberCoroutineScope()
  val permissionState = rememberPermissionState(Permission.Notification)

  LaunchedEffect(permissionState.status, checkPermission) {
    if (checkPermission) {
      when {
        permissionState.status.isGranted -> {
          checkPermission = false
        }

        permissionState.status.shouldShowRationale -> {
          showRationaleDialog = true
          checkPermission = false
        }

        else -> {
          permissionState.openAppSettings()
          checkPermission = false
        }
      }
    }
  }

  if (showAlreadyGrantedDialog) {
    BasicAlertDialog(
      onDismissRequest = { showAlreadyGrantedDialog = false },
      properties = DialogProperties(
        dismissOnBackPress = true,
        dismissOnClickOutside = true
      )
    ) {
      Card {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = stringResource(Res.string.notification_permission_already_granted),
            style = MaterialTheme.typography.bodyLarge
          )
          Spacer(modifier = Modifier.height(16.dp))
          TextButton(
            onClick = { showAlreadyGrantedDialog = false },
            modifier = Modifier.align(Alignment.End)
          ) {
            Text(text = stringResource(Res.string.ok))
          }
        }
      }
    }
  }

  if (showExplanationDialog) {
    BasicAlertDialog(
      onDismissRequest = { showExplanationDialog = false },
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
            showExplanationDialog = false
            scope.launch { permissionState.launchPermissionRequest() }
            checkPermission = true
          }) {
            Text(text = stringResource(Res.string.button_enable))
          }
          TextButton(onClick = {
            showExplanationDialog = false
          }) {
            Text(text = stringResource(Res.string.button_skip))
          }
        }
      }
    }
  }
  SettingsMenuLink(
    title = { Text(text = stringResource(Res.string.onboarding_notification_permission_title)) },
    onClick = {
      if (permissionState.status.isGranted) {
        showAlreadyGrantedDialog = true
      } else {
        showExplanationDialog = true
      }
    },
    icon = {
      Icon(
        imageVector = Icons.Default.Notifications,
        contentDescription = "Notifications"
      )
    },
    colors = ListItemDefaults.colors(
      containerColor = MaterialTheme.colorScheme.surfaceContainer
    )
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

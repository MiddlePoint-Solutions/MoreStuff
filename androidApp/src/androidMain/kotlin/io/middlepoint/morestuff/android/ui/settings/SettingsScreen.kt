package io.middlepoint.morestuff.android.ui.settings

import MoreStuff.androidApp.BuildConfig
import android.content.res.Configuration
import android.content.res.Resources
import android.widget.Toast
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
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.ModeStandby
import androidx.compose.material.icons.filled.Translate
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import io.middlepoint.morestuff.android.R
import io.middlepoint.morestuff.android.data.Constants.DISCORD_INVITE_LINK
import io.middlepoint.morestuff.android.data.Constants.PRIVACY_POLICY_LINK
import io.middlepoint.morestuff.android.data.Constants.TELEGRAM_INVITE_LINK
import io.middlepoint.morestuff.android.ui.components.SettingsTopBar
import io.middlepoint.morestuff.shared.navigation.ChildStack
import io.middlepoint.morestuff.android.ui.priority.PriorityTimePicker
import io.middlepoint.morestuff.android.ui.scopes.ScopesScreen
import io.middlepoint.morestuff.android.ui.theme.MoreStuffSettingTheme
import io.middlepoint.morestuff.android.ui.theme.MoreStuffTheme
import io.middlepoint.morestuff.shared.domain.enums.AppTheme
import io.middlepoint.morestuff.shared.domain.enums.Language
import io.middlepoint.morestuff.shared.domain.nav.SettingScreen
import io.middlepoint.morestuff.shared.domain.nav.SettingScreen.AboutLibraries
import io.middlepoint.morestuff.shared.domain.nav.SettingScreen.Developer
import io.middlepoint.morestuff.shared.domain.nav.SettingScreen.Root
import io.middlepoint.morestuff.shared.domain.nav.SettingScreen.Scopes
import io.middlepoint.morestuff.shared.ui.components.rememberAppSettingState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun SettingsScreen(
  onBack: () -> Unit,
  viewModel: SettingsViewModel = koinViewModel(),
) {

  val navigation = remember { StackNavigation<SettingScreen>() }
  val model by viewModel.models.collectAsState()


  ChildStack(
    source = navigation,
    initialStack = { listOf(Root) },
    modifier = Modifier.background(Color.Transparent),
    key = "SettingsStack",
    handleBackButton = true,
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
          showDevSettings = { navigation.push(Developer) },
          showScopesSettings = { navigation.push(Scopes) },
          showLibraries = { navigation.push(AboutLibraries) },
        )
      }

      Developer -> DevSettingsScreen(onBack = navigation::pop)

      Scopes -> ScopesScreen(onBack = navigation::pop)

      AboutLibraries -> AboutLibrariesScreen(onBack = navigation::pop)
    }
  }
}

@Composable
private fun SettingsContent(
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
          title = stringResource(id = R.string.settings)
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
                Text(text = stringResource(id = R.string.developer_settings))
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

  val resources = LocalContext.current.resources

  val themeOptions = remember {
    AppTheme.entries.map { it.displayTitle(resources) }
  }

  val state = rememberAppSettingState(
    defaultValue = defaultValue,
    valueChanged = themeSelected,
  )

  // TODO: SettingsList is not available anymore, need to implement the dialog ourselves
//  SettingsList(
//    state = state,
//    title = {
//      Text(
//        text = stringResource(R.string.select_theme),
//      )
//    },
//    items = themeOptions,
//    icon = {
//      Icon(
//        imageVector = Icons.Default.ColorLens,
//        contentDescription = stringResource(R.string.cd_select_theme)
//      )
//    },
//    closeDialogDelay = 0,
//    useSelectedValueAsSubtitle = false,
//  )
}

private fun AppTheme.displayTitle(res: Resources): String = when (this) {
  AppTheme.System -> res.getString(R.string.theme_system)
  AppTheme.Light -> res.getString(R.string.theme_light)
  AppTheme.Dark -> res.getString(R.string.theme_dark)
}

@Composable
private fun About(
  modifier: Modifier = Modifier,
  devSettingsEnabled: Boolean,
  enableDevSettings: () -> Unit,
  showLibraries: () -> Unit,
) {

  val uriHandler = LocalUriHandler.current
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  var devSettingsCounter by remember { mutableIntStateOf(7) }

  Surface(modifier = modifier) {
    HorizontalDivider()

    Column(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    ) {

      Text(
        text = stringResource(id = R.string.join_community),
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
            imageVector = ImageVector.vectorResource(R.drawable.ic_discord),
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
            imageVector = ImageVector.vectorResource(R.drawable.ic_telegram),
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
          text = stringResource(R.string.privacy_policy),
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
          text = stringResource(R.string.open_source_libraries),
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
          color = MaterialTheme.colorScheme.onBackground
        )
      }

    }
  }
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
        Text(text = stringResource(R.string.set_review_time))
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
private fun SelectLanguage(
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

  // TODO:
//  SettingsList(
//    state = state,
//    title = {
//      Column {
//        Text(text = stringResource(R.string.select_language))
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
//        contentDescription = stringResource(R.string.select_language)
//      )
//    },
//    closeDialogDelay = 0,
//    useSelectedValueAsSubtitle = false,
//  )
}

private fun Language.displayTitle(res: Resources): String = when (this) {
  Language.Device -> res.getString(R.string.language_device_default)
  Language.English -> res.getString(R.string.language_english)
  Language.Spanish -> res.getString(R.string.language_spanish)
  Language.Hebrew -> res.getString(R.string.language_hebrew)
  Language.Russian -> res.getString(R.string.language_russian)
  Language.Catalan -> res.getString(R.string.language_catalan)
}

@Composable
private fun ScopeSettings(onClick: () -> Unit) {
  SettingsMenuLink(
    title = { Text(text = stringResource(R.string.title_scopes)) },
    onClick = onClick,
    icon = {
      Icon(
        imageVector = Icons.Default.ModeStandby,
        contentDescription = stringResource(R.string.select_language)
      )
    }
  )
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
      showDevSettings = {},
      showScopesSettings = {},
      model = SettingsState(),
      enableDevSettings = {},
      selectAppTheme = {},
      selectLanguage = {},
      setReviewTime = { _, _ -> }


    )
  }
}

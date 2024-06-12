package io.middlepoint.morestuff.android.ui.utils

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.middlepoint.morestuff.shared.ui.screen.settings.SettingsContent
import io.middlepoint.morestuff.shared.ui.screen.settings.SettingsState
import io.middlepoint.morestuff.shared.ui.theme.MoreStuffTheme

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
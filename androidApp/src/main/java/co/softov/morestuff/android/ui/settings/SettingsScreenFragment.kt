package co.softov.morestuff.android.ui.settings

import androidx.compose.runtime.Composable
import co.softov.morestuff.android.app.presentation.compose.BaseComposeFragment


class SettingsFragment : BaseComposeFragment() {

    @Composable
    override fun ScreenContent() {
        SettingsScreen()
    }

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}
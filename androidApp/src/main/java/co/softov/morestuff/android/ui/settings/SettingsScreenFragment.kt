package co.softov.morestuff.android.ui.settings

import android.os.Bundle
import androidx.compose.runtime.Composable
import co.softov.morestuff.android.app.presentation.compose.BaseComposeFragment


class SettingsFragment : BaseComposeFragment() {

    @Composable
    override fun ScreenContent(
        args: Bundle?,
        showKeyboard: () -> Unit,
        hideKeyboard: () -> Unit,
        dismissDialog: () -> Unit,
    ) {
        SettingsScreen()
    }

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}
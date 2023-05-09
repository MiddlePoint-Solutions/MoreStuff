package co.softov.morestuff.android.ui.schedule

import android.os.Bundle
import androidx.compose.runtime.Composable
import co.softov.morestuff.android.app.presentation.compose.BaseComposeFragment
import timber.log.Timber

class PriorityFragment : BaseComposeFragment() {

    @Composable
    override fun ScreenContent(
        args: Bundle?,
        showKeyboard: () -> Unit,
        hideKeyboard: () -> Unit,
        dismissDialog: () -> Unit
    ) {
        Timber.d("ScreenContent")
        PriorityContent()
    }

}
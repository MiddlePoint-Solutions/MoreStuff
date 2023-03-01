package co.softov.morestuff.android.app.presentation.compose

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import co.softov.morestuff.android.app.extensions.hideKeyboard
import co.softov.morestuff.android.app.extensions.showKeyboard
import co.softov.morestuff.android.ui.compose.viewMigration
import timber.log.Timber

abstract class BaseComposeFragment : Fragment(), ComposeContent {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view as ComposeView).viewMigration {
            ScreenContent(
                args = arguments,
                showKeyboard = { showKeyboard() },
                hideKeyboard = { hideKeyboard() },
                dismissDialog = {}
            )
        }
    }
}

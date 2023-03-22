package co.softov.morestuff.android.ui.review

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import co.softov.morestuff.android.app.presentation.compose.BaseComposeFragment
import co.softov.morestuff.android.ui.compose.viewMigration
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ReviewFragment : BaseComposeFragment() {

    @Composable
    override fun ScreenContent(
        args: Bundle?,
        showKeyboard: () -> Unit,
        hideKeyboard: () -> Unit,
        dismissDialog: () -> Unit
    ) {
        Timber.d("ScreenContent")
        ReviewContent()
    }

}
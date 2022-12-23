package co.softov.morestuff.android.ui.review

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import co.softov.morestuff.android.ui.compose.viewMigration
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ReviewFragment : BottomSheetDialogFragment() {

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)


        val height = getWindowHeight()
        val view = ComposeView(requireContext()).apply {
            viewMigration {
                Timber.d("ReviewFragment")
                val nestedScrollInterop = rememberNestedScrollInteropConnection()
                ReviewContent(modifier = Modifier.nestedScroll(nestedScrollInterop))
            }
        }
        dialog.setContentView(
            view,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        )

        val bottomSheetDialog = dialog as BottomSheetDialog
        bottomSheetDialog.behavior.state = STATE_EXPANDED
        return dialog
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = requireActivity().resources.displayMetrics
        return displayMetrics.heightPixels
    }
}
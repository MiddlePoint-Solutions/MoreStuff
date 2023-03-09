package co.softov.morestuff.android.app.presentation.compose

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import co.softov.morestuff.android.app.extensions.hideKeyboard
import co.softov.morestuff.android.app.extensions.showKeyboard
import co.softov.morestuff.android.ui.compose.viewMigration
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseComposeBottomSheetFragment : BottomSheetDialogFragment(), ComposeContent {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        val view = ComposeView(requireContext()).apply {
            viewMigration {
                ScreenContent(
                    arguments,
                    showKeyboard = { dialog.showKeyboard() },
                    hideKeyboard = { dialog.hideKeyboard() },
                    dismissDialog = { dismiss() }
                )
            }
        }

        dialog.setContentView(
            view,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getWindowHeight())
        )

        val bottomSheetDialog = dialog as BottomSheetDialog
        setBottomSheetBehavior(bottomSheetDialog.behavior)

        return dialog
    }

    @Composable
    abstract override fun ScreenContent(
        args: Bundle?,
        showKeyboard: () -> Unit,
        hideKeyboard: () -> Unit,
        dismissDialog: () -> Unit,
    )

    open fun setBottomSheetBehavior(behavior: BottomSheetBehavior<FrameLayout>) {
        behavior.isFitToContents = true
        behavior.isDraggable = true
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = requireActivity().resources.displayMetrics
        return displayMetrics.heightPixels
    }

}

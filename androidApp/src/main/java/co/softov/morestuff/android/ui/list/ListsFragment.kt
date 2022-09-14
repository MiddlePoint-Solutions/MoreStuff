package co.softov.morestuff.android.ui.list

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import co.softov.morestuff.android.ui.compose.viewMigration
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ListsFragment : BottomSheetDialogFragment() {

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        val height = getWindowHeight()
        val view = ComposeView(requireContext()).apply {

            viewMigration {
                val nestedScrollInterop = rememberNestedScrollInteropConnection()
                ListsContent(
                    modifier = Modifier
                        .systemBarsPadding()
                        .nestedScroll(nestedScrollInterop)
                )
            }
        }
        dialog.setContentView(
            view,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        )

        val bottomSheetDialog = dialog as BottomSheetDialog
        bottomSheetDialog.behavior.isFitToContents = false
        bottomSheetDialog.behavior.peekHeight = height / 2
        return dialog
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = requireActivity().resources.displayMetrics
        return displayMetrics.heightPixels
    }
}
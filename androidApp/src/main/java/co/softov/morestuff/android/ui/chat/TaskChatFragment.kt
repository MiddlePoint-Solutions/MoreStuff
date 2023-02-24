package co.softov.morestuff.android.ui.chat

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.fragment.app.ListFragment
import co.softov.morestuff.android.ui.compose.viewMigration
import co.softov.morestuff.android.ui.list.ListsFragment
import co.softov.morestuff.android.ui.main.MainConductor
import co.softov.morestuff.android.ui.main.MainContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TaskChatFragment : BottomSheetDialogFragment() {

    private val conductor = object : MainConductor {

        override fun showTaskList() {
            ListsFragment().show(parentFragmentManager, ListFragment::javaClass.name)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        val height = getWindowHeight()
        val view = ComposeView(requireContext()).apply {

            viewMigration {
                val nestedScrollInterop = rememberNestedScrollInteropConnection()
                MainContent(
                    conductor,
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
        bottomSheetDialog.behavior.isDraggable = false
        bottomSheetDialog.behavior.isHideable = true
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return dialog
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = requireActivity().resources.displayMetrics
        return displayMetrics.heightPixels
    }
}
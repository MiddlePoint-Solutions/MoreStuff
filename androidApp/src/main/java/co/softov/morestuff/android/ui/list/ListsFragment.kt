package co.softov.morestuff.android.ui.list

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.fragment.app.ListFragment
import co.softov.morestuff.android.app.presentation.compose.BaseComposeBottomSheetFragment
import co.softov.morestuff.android.ui.Screens
import co.softov.morestuff.android.ui.chat.TaskChatFragment
import co.softov.morestuff.android.ui.compose.TestContent
import co.softov.morestuff.android.ui.compose.viewMigration
import com.github.terrakok.cicerone.Router
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject

class ListsFragment : BaseComposeBottomSheetFragment() {

    private val router: Router by inject()

    @Composable
    override fun ScreenContent() {
        val nestedScrollInterop = rememberNestedScrollInteropConnection()
        ListsContent(
            modifier = Modifier
                .systemBarsPadding()
                .nestedScroll(nestedScrollInterop)
        ) {
            router.navigateTo(Screens.TaskChat)
        }
    }

    override fun setBottomSheetBehavior(behavior: BottomSheetBehavior<FrameLayout>) {
        behavior.isFitToContents = false
        behavior.peekHeight = getWindowHeight() / 2
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = requireActivity().resources.displayMetrics
        return displayMetrics.heightPixels
    }
}
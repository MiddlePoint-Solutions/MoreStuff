package co.softov.morestuff.android.ui.list

import android.os.Bundle
import android.widget.FrameLayout
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import co.softov.morestuff.android.app.navigation.AppRouter
import co.softov.morestuff.android.app.presentation.compose.BaseComposeBottomSheetFragment
import co.softov.morestuff.android.ui.Screens
import com.github.terrakok.cicerone.Router
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.android.ext.android.inject

class ListsFragment : BaseComposeBottomSheetFragment() {

    private val router: AppRouter by inject()

    @Composable
    override fun ScreenContent(
        args: Bundle?,
        showKeyboard: () -> Unit,
        hideKeyboard: () -> Unit,
        dismissDialog: () -> Unit
    ) {
        val nestedScrollInterop = rememberNestedScrollInteropConnection()
        ListsContent(
            modifier = Modifier
                .systemBarsPadding()
                .nestedScroll(nestedScrollInterop)
        ) {
            dismiss()
            router.navigateTo(Screens.taskChat(it))
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
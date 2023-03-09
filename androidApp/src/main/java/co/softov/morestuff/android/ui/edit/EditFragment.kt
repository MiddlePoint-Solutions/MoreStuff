package co.softov.morestuff.android.ui.edit

import android.os.Bundle
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.presentation.compose.BaseComposeBottomSheetFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior

class EditFragment : BaseComposeBottomSheetFragment() {

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun ScreenContent(
        args: Bundle?,
        showKeyboard: () -> Unit,
        hideKeyboard: () -> Unit,
        dismissDialog: () -> Unit
    ) {
        /*
        TODO: create TaskEditViewModel with different edit methods
         1. Change task title
         2. Change task schedule
         */
        EditTaskTitle(
            dismissAction = {
                hideKeyboard()
                dismissDialog()
            },
            title = "Implement task title editing",
        )

        LaunchedEffect(Unit) {
            showKeyboard()
        }
    }

    override fun setBottomSheetBehavior(behavior: BottomSheetBehavior<FrameLayout>) {
        behavior.isFitToContents = false
        behavior.isDraggable = true
        behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

}
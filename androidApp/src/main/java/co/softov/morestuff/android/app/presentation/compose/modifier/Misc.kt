package co.softov.morestuff.android.app.presentation.compose.modifier

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.runOnTap(
    action: () -> Unit
) = this.pointerInput(Unit) {
    detectTapGestures(onTap = {
        action()
    })
}
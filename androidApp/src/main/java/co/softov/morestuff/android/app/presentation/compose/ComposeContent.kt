package co.softov.morestuff.android.app.presentation.compose

import android.os.Bundle
import androidx.compose.runtime.Composable

interface ComposeContent {

    @Composable
    fun ScreenContent(
        args: Bundle?,
        showKeyboard: () -> Unit,
        hideKeyboard: () -> Unit,
        dismissDialog: () -> Unit,
    )

}
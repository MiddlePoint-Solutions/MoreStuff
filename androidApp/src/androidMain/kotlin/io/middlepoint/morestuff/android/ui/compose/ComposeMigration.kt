package io.middlepoint.morestuff.android.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import io.middlepoint.morestuff.android.ui.theme.MoreStuffTheme

/**
 * Extension for easy compose migration
 */
fun ComposeView.viewMigration(content: @Composable () -> Unit) {
    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
    setContent {
        MoreStuffTheme {
            content()
        }
    }
}
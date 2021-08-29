package co.softov.morestuff.android.presentation.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import co.softov.morestuff.android.presentation.theme.MoreStuffTheme

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
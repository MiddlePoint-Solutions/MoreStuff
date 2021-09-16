package co.softov.morestuff.android.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.ViewWindowInsetObserver

/**
 * Extension for easy compose migration
 */
fun ComposeView.viewMigration(content: @Composable () -> Unit) {
    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

    // Create a ViewWindowInsetObserver using this view, and call start() to
    // start listening now. The WindowInsets instance is returned, allowing us to
    // provide it to AmbientWindowInsets in our content below.
    val windowInsets = ViewWindowInsetObserver(this)
        // We use the `windowInsetsAnimationsEnabled` parameter to enable animated
        // insets support. This allows our `ConversationContent` to animate with the
        // on-screen keyboard (IME) as it enters/exits the screen.
        .start(windowInsetsAnimationsEnabled = true)

    setContent {
        ProvideWindowInsets(consumeWindowInsets = false) {
            CompositionLocalProvider(
                LocalWindowInsets provides windowInsets,
            ) {
                MoreStuffTheme {
                    content()
                }
            }
        }
    }
}
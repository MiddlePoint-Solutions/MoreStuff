package co.softov.morestuff.android.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SlideAnimation(
    visibleState: MutableTransitionState<Boolean>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visibleState = visibleState,
        modifier = modifier,
        enter = slideInVertically { it * 2 },
        exit = slideOutVertically { (it * 1.5).toInt() }
    ) {
        content()
    }
}
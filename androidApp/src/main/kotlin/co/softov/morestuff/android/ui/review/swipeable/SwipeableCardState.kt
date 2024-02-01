package co.softov.morestuff.android.ui.review.swipeable

import androidx.compose.animation.core.*
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


enum class SwipeDirection {
     None , Left, Right, Up, Down
}

enum class FlipState {
    Front, Back
}

@Composable
fun rememberSwipeableCardState(key: Any = Unit): SwipeableCardState {
    val screenWidth = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }
    val screenHeight = with(LocalDensity.current) {
        LocalConfiguration.current.screenHeightDp.dp.toPx()
    }

    return remember(key) {
        SwipeableCardState(
            screenWidth,
            screenHeight,
        )
    }
}


class SwipeableCardState(
    internal val maxWidth: Float,
    internal val maxHeight: Float,
) {
    val offset = Animatable(offset(0f, 0f), Offset.VectorConverter)
    val flip = Animatable(0f, Float.VectorConverter)
    val scale = Animatable(offset(1f, 1f), Offset.VectorConverter)
    val alpha = Animatable(1f, Float.VectorConverter)

    /**
     * The [SwipeDirection] the card was swiped at.
     *
     * Null value means the card has not been swiped fully yet.
     */
    var isSwiped: Boolean by mutableStateOf(false)
        private set

    /**
     * The [SwipeDirection] the card was swiped at.
     *
     * Null value means the card has not been swiped fully yet.
     */
    var swipedDirection: SwipeDirection? by mutableStateOf(null)
        private set

    /**
     * The [FlipState] of the card.
     *
     * Null value means the card has not been swiped fully yet.
     */
    private var flipState: FlipState by mutableStateOf(FlipState.Front)

    suspend fun reset(animationSpec: AnimationSpec<Offset> = tween(400)) = coroutineScope {
        swipedDirection = null
        launch {
            awaitAll(
                async { offset.animateTo(offset(0f, 0f), animationSpec) },
                async { scale.animateTo(offset(1f, 1f), animationSpec) },
                async { alpha.animateTo(1f, tween(200)) }
            )
        }.invokeOnCompletion {
            isSwiped = false
        }
    }

    suspend fun undo() {
        Timber.d("undo")
        reset(tween(200))
    }

    suspend fun fling(direction: SwipeDirection, velocity: Velocity) = coroutineScope {
        isSwiped = true
        val endX = maxWidth * 1.2f
        val endY = maxHeight

        val animation = when (direction) {
            SwipeDirection.Left -> offset.animateTo(
                offset(x = -endX),
                animationSpec = tween(250)
            )

            SwipeDirection.Right -> offset.animateTo(
                offset(x = endX),
                animationSpec = tween(250)
            )

            SwipeDirection.Up -> offset.animateTo(
                offset(y = -endY),
                animationSpec = tween(250)
            )

            SwipeDirection.Down -> offset.animateTo(
                offset(y = endY),
                animationSpec = tween(250)
            )

            SwipeDirection.None -> alpha.animateTo(0f, tween(250))
        }
        launch {
            awaitAll(
                async { animation },
                async { alpha.animateTo(0f, tween(200)) }
            )
        }.invokeOnCompletion {
            swipedDirection = direction
        }
    }

    suspend fun swipe(
        direction: SwipeDirection,
        animationSpec: AnimationSpec<Offset> = tween(300)
    ) = coroutineScope {
        isSwiped = true
        val endX = maxWidth * 1.2f
        val endY = maxHeight
        val animation = when (direction) {
            SwipeDirection.Left -> offset.animateTo(offset(x = -endX), animationSpec)
            SwipeDirection.Right -> offset.animateTo(offset(x = endX), animationSpec)
            SwipeDirection.Up -> offset.animateTo(offset(y = -endY), animationSpec)
            SwipeDirection.Down -> offset.animateTo(offset(y = endY), animationSpec)
            SwipeDirection.None -> alpha.animateTo(0f, tween(250))
        }

        launch {
            awaitAll(
                async { animation },
                async { alpha.animateTo(0f, tween(300)) }
            )
        }.invokeOnCompletion {
            swipedDirection = direction
        }
    }

    suspend fun onComplete() = coroutineScope {
        isSwiped = true
        launch {
            awaitAll(
                async { scale.animateTo(offset(1.5f, 1.5f), tween(300)) },
                async { alpha.animateTo(0f, tween(300)) }
            )
        }.invokeOnCompletion {
            swipedDirection = SwipeDirection.None
        }
    }

    private fun offset(x: Float = offset.value.x, y: Float = offset.value.y): Offset {
        return Offset(x, y)
    }

    internal suspend fun drag(x: Float, y: Float) {
        offset.animateTo(offset(x, y))
    }

    suspend fun flip() {
        when (flipState) {
            FlipState.Front -> flipToBack()
            FlipState.Back -> flipToFront()
        }
    }

    private suspend fun flipToFront() {
        flipState = FlipState.Front
        flip.animateTo(0f, tween(500))
//        launch { flip.animateTo(INITIAL_FLIP_ANGLE, tween(FLIP_ROTATION_DURATION_IN_MILLIS)) }
//            launch { frontAlpha.animateTo(1f, tween(CARD_FRONT_ALPHA_DURATION_IN_MILLIS)) }
//            launch { backAlpha.animateTo(0f, tween(CARD_REVERSE_ALPHA_DURATION_IN_MILLIS)) }
    }

    private suspend fun flipToBack() {
        flipState = FlipState.Back
        flip.animateTo(180f, tween(500))
//            launch { flip.animateTo(FINAL_FLIP_ANGLE, tween(FLIP_ROTATION_DURATION_IN_MILLIS)) }
//            launch { frontAlpha.animateTo(0f, tween(CARD_REVERSE_ALPHA_DURATION_IN_MILLIS)) }
//            launch { backAlpha.animateTo(1f, tween(CARD_FRONT_ALPHA_DURATION_IN_MILLIS)) }
    }
}

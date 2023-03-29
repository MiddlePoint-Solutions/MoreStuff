package co.softov.morestuff.android.ui.review.swipeable

import androidx.compose.animation.core.*
import androidx.compose.animation.splineBasedDecay
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import timber.log.Timber


enum class Direction {
    Left, Right, Up, Down
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

    /**
     * The [Direction] the card was swiped at.
     *
     * Null value means the card has not been swiped fully yet.
     */
    var isSwiped: Boolean by mutableStateOf(false)
        private set

    /**
     * The [Direction] the card was swiped at.
     *
     * Null value means the card has not been swiped fully yet.
     */
    var swipedDirection: Direction? by mutableStateOf(null)
        private set

    /**
     * The [FlipState] of the card.
     *
     * Null value means the card has not been swiped fully yet.
     */
    private var flipState: FlipState by mutableStateOf(FlipState.Front)

    suspend fun reset(animationSpec: AnimationSpec<Offset> = tween(400)) {
        swipedDirection = null
        offset.animateTo(offset(0f, 0f), animationSpec)
        isSwiped = false
    }

    suspend fun undo() {
        Timber.d("undo")
        reset(tween(200))
    }

    suspend fun fling(direction: Direction, velocity: Velocity) {
        isSwiped = true
        val endX = maxWidth * 1.1f
        val endY = maxHeight



        when (direction) {
            Direction.Left -> offset.animateTo(
                offset(x = -endX),
                initialVelocity = offset(x = velocity.x),
                animationSpec = tween(250)
            )
            Direction.Right -> offset.animateTo(
                offset(x = endX),
                initialVelocity = offset(x = velocity.x),
                animationSpec = spring()
            )
            Direction.Up -> offset.animateTo(
                offset(y = -endY),
                initialVelocity = offset(y = velocity.y),
                animationSpec = tween(250)
            )
            Direction.Down -> offset.animateTo(
                offset(y = endY),
                initialVelocity = offset(y = velocity.y),
                animationSpec = tween(250)
            )
        }
        swipedDirection = direction
    }

    suspend fun swipe(direction: Direction, animationSpec: AnimationSpec<Offset> = tween(300)) {
        isSwiped = true
        val endX = maxWidth * 1.5f
        val endY = maxHeight
        when (direction) {
            Direction.Left -> offset.animateTo(offset(x = -endX), animationSpec)
            Direction.Right -> offset.animateTo(offset(x = endX), animationSpec)
            Direction.Up -> offset.animateTo(offset(y = -endY), animationSpec)
            Direction.Down -> offset.animateTo(offset(y = endY), animationSpec)
        }
        swipedDirection = direction
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

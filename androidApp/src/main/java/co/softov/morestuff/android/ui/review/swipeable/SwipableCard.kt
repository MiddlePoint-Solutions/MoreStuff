package co.softov.morestuff.android.ui.review.swipeable

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.abs

/**
 * Enables Tinder like swiping gestures.
 *
 * @param state The current state of the swipeable card. Use [rememberSwipeableCardState] to create.
 * @param onSwiped will be called once a swipe gesture is completed. The given [Direction] will indicate which side the gesture was performed on.
 * @param onSwipeCancel will be called when the gesture is stopped before reaching the minimum threshold to be treated as a full swipe
 * @param blockedDirections the directions which will not trigger a swipe. By default only horizontal swipes are allowed.
 */
@ExperimentalSwipeableCardApi
fun Modifier.swipableCard(
    state: SwipeableCardState,
    onSwiped: (Direction) -> Unit = {},
    onSwipeCancel: () -> Unit = {},
    blockedDirections: List<Direction> = listOf(Direction.Up, Direction.Down),
) = pointerInput(state) {
    coroutineScope {
        val velocityTracker = VelocityTracker()
        detectDragGestures(
            onDragStart = {
                Timber.d("DRAG: START(${it.x}, ${it.y})")
            },
            onDragCancel = {
                Timber.d("DRAG: CANCEL")
                launch {
                    state.reset()
                    onSwipeCancel()
                }
            },
            onDrag = { change, dragAmount ->
                if (state.isSwiped) {
                    change.consume()
                } else {
                    velocityTracker.addPointerInputChange(change)
                    launch {
                        val original = state.offset.targetValue
                        val summed = original + dragAmount
                        val newValue = Offset(
                            x = summed.x.coerceIn(-state.maxWidth, state.maxWidth),
                            y = summed.y.coerceIn(-state.maxHeight, state.maxHeight)
                        )
                        if (change.positionChange() != Offset.Zero) change.consume()
                        state.drag(newValue.x, newValue.y)
                    }
                }
            },
            onDragEnd = {
                launch {
                    val coercedOffset = state.offset.targetValue
                        .coerceIn(
                            blockedDirections,
                            maxHeight = state.maxHeight,
                            maxWidth = state.maxWidth
                        )

                    val velocity = velocityTracker.calculateVelocity()
                    Timber.d("DRAG: END(${coercedOffset.x}, ${coercedOffset.y}), VELOCITY(${velocity.x},${velocity.y})")

                    val velocitySwipe = isVelocitySwipe(velocity)

                    if (hasNotTravelledEnough(state, coercedOffset) && !velocitySwipe) {
                        state.reset()
                        onSwipeCancel()
                    } else {
                        val horizontalTravel = abs(state.offset.targetValue.x)
                        val verticalTravel = abs(state.offset.targetValue.y)

                        if (horizontalTravel > verticalTravel) {
                            if (state.offset.targetValue.x > 0) {
                                state.fling(Direction.Right, velocity)
                                onSwiped(Direction.Right)
                            } else {
                                state.fling(Direction.Left, velocity)
                                onSwiped(Direction.Left)
                            }
                        } else {
                            if (state.offset.targetValue.y < 0) {
                                state.fling(Direction.Up, velocity)
                                onSwiped(Direction.Up)
                            } else {
                                state.fling(Direction.Down, velocity)
                                onSwiped(Direction.Down)
                            }
                        }
                    }
                }
            }
        )
    }
}
    .graphicsLayer {
        translationX = state.offset.value.x
        translationY = state.offset.value.y
        rotationZ = (state.offset.value.x / 60).coerceIn(-40f, 40f)
        rotationY = state.flip.value
    }

private fun isVelocitySwipe(velocity: Velocity): Boolean {
    val vX = abs(velocity.x)
    val vY = abs(velocity.y)
    return vX > 5000 || vY > 5000
}

private fun Offset.coerceIn(
    blockedDirections: List<Direction>,
    maxHeight: Float,
    maxWidth: Float,
): Offset {
    return copy(
        x = x.coerceIn(
            if (blockedDirections.contains(Direction.Left)) {
                0f
            } else {
                -maxWidth
            },
            if (blockedDirections.contains(Direction.Right)) {
                0f
            } else {
                maxWidth
            }
        ),
        y = y.coerceIn(
            if (blockedDirections.contains(Direction.Up)) {
                0f
            } else {
                -maxHeight
            },
            if (blockedDirections.contains(Direction.Down)) {
                0f
            } else {
                maxHeight
            }
        )
    )
}

private fun hasNotTravelledEnough(
    state: SwipeableCardState,
    offset: Offset,
): Boolean {
    return abs(offset.x) < state.maxWidth / 4.5 &&
            abs(offset.y) < state.maxHeight / 4.5
}


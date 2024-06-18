package io.middlepoint.morestuff.shared.ui.components.swipeable

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.unit.Velocity
import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.android.ui.review.swipeable.ExperimentalSwipeableCardApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Enables Tinder like swiping gestures.
 *
 * @param state The current state of the swipeable card. Use [rememberSwipeableCardState] to create.
 * @param onSwiped will be called once a swipe gesture is completed. The given [SwipeDirection] will indicate which side the gesture was performed on.
 * @param onSwipeCancel will be called when the gesture is stopped before reaching the minimum threshold to be treated as a full swipe
 * @param blockedDirections the directions which will not trigger a swipe. By default only horizontal swipes are allowed.
 */
@ExperimentalSwipeableCardApi
fun Modifier.swipableCard(
    state: SwipeableCardState,
    enabled: Boolean = true,
    onSwiped: (SwipeDirection) -> Unit = {},
    onDrag: (Boolean) -> Unit = {},
    onSwipeCancel: () -> Unit = {},
    blockedDirections: List<SwipeDirection> = listOf(),
) = pointerInput(state) {
    coroutineScope {
        val velocityTracker = VelocityTracker()
        detectDragGestures(
            onDragStart = {
                Logger.d { "$it" }
                onDrag(true)
            },
            onDragCancel = {
                launch {
                    state.reset()
                    onSwipeCancel()
                }
            },
            onDrag = { change, dragAmount ->
                Logger.d { "$dragAmount" }
                if (state.isSwiped || !enabled) {
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
                onDrag(false)
                launch {
                    val coercedOffset = state.offset.targetValue
                        .coerceIn(
                            blockedDirections,
                            maxHeight = state.maxHeight,
                            maxWidth = state.maxWidth
                        )

                    val velocity = velocityTracker.calculateVelocity()

                    val velocitySwipe = isVelocitySwipe(velocity)

                    if (hasNotTravelledEnough(state, coercedOffset) && !velocitySwipe) {
                        state.reset()
                        onSwipeCancel()
                    } else {
                        val horizontalTravel = abs(state.offset.targetValue.x)
                        val verticalTravel = abs(state.offset.targetValue.y)

                        if (horizontalTravel > verticalTravel) {
                            if (state.offset.targetValue.x > 0) {
                                state.fling(SwipeDirection.Right, velocity)
                                onSwiped(SwipeDirection.Right)
                            } else {
                                state.fling(SwipeDirection.Left, velocity)
                                onSwiped(SwipeDirection.Left)
                            }
                        } else {
                            if (state.offset.targetValue.y < 0) {
                                state.fling(SwipeDirection.Up, velocity)
                                onSwiped(SwipeDirection.Up)
                            } else {
                                state.fling(SwipeDirection.Down, velocity)
                                onSwiped(SwipeDirection.Down)
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
        scaleX = state.scale.value.x
        scaleY = state.scale.value.y
        alpha = state.alpha.value
    }

private fun isVelocitySwipe(velocity: Velocity): Boolean {
    val vX = abs(velocity.x)
    val vY = abs(velocity.y)
    return vX > 5000 || vY > 5000
}

private fun Offset.coerceIn(
    blockedDirections: List<SwipeDirection>,
    maxHeight: Float,
    maxWidth: Float,
): Offset {
    return copy(
        x = x.coerceIn(
            if (blockedDirections.contains(SwipeDirection.Left)) {
                0f
            } else {
                -maxWidth
            },
            if (blockedDirections.contains(SwipeDirection.Right)) {
                0f
            } else {
                maxWidth
            }
        ),
        y = y.coerceIn(
            if (blockedDirections.contains(SwipeDirection.Up)) {
                0f
            } else {
                -maxHeight
            },
            if (blockedDirections.contains(SwipeDirection.Down)) {
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
    return abs(offset.x) < state.maxWidth / 5.5 &&
            abs(offset.y) < state.maxHeight / 6.5
}


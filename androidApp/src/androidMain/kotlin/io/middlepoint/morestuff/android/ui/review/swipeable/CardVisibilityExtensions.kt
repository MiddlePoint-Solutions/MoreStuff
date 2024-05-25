package io.middlepoint.morestuff.android.ui.review.swipeable

import androidx.compose.ui.geometry.Offset
import io.middlepoint.morestuff.android.ui.model.ReviewItemUiModel


fun List<Pair<ReviewItemUiModel, SwipeableCardState>>.lastSwipedItem() =
    reversed().firstOrNull { firstVisibleItem(it) }?.run {
        getOrNull(indexOf(this) + 1)
    } ?: firstOrNull()

fun firstVisibleItem(it: Pair<ReviewItemUiModel, SwipeableCardState>) =
    it.second.offset.value == Offset(0f, 0f) && !it.second.isSwiped

fun List<Pair<ReviewItemUiModel, SwipeableCardState>>.firstVisibleOrNull() =
    reversed().firstOrNull { firstVisibleItem(it) }

fun List<Pair<ReviewItemUiModel, SwipeableCardState>>.firstVisibleStateOrNull() =
    firstVisibleOrNull()?.second
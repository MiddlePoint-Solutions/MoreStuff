package co.softov.morestuff.android.ui.schedule

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.ui.compose.NoFlingDismissState
import co.softov.morestuff.android.ui.compose.NoFlingSwipeToDismiss
import co.softov.morestuff.android.ui.compose.rememberNoFlingDismissState
import co.softov.morestuff.android.ui.model.TaskUiModel

@Composable
fun ScopeContent(
    tasks: List<TaskUiModel>,
    onItemClick: (taskId: Long) -> Unit,
    onItemLongClick: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
    ) {
        itemsIndexed(
            items = tasks,
            key = { _, task -> task.id }
        ) { index, item ->

            val isLast by remember(index) {
                derivedStateOf { index == tasks.lastIndex }
            }

            PriorityItem(
                task = item,
                onClick = { onItemClick(item.id) },
                onLongClick = { onItemLongClick(item.id) }
            )

            Row(
                modifier = Modifier.fillParentMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (!isLast) {
                    Divider(
                        thickness = Dp.Hairline,
                        modifier = Modifier.fillMaxWidth(0.8f),
                        color = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SwipeablePriorityItem(
    showTaskOptions: (taskId: Long) -> Unit,
    item: TaskUiModel,
    toggleQuickReminder: (taskId: Long) -> Unit,
    content: @Composable RowScope.() -> Unit,
) {

    val haptic = LocalHapticFeedback.current
    var willDismissDirection: DismissDirection? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(willDismissDirection) {
        if (willDismissDirection != null) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    val dismissState = rememberNoFlingDismissState(
        positionalThreshold = { 130.dp.toPx() },
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                DismissValue.Default -> false
                DismissValue.DismissedToEnd -> {
                    showTaskOptions(item.id)
                    false
                }

                DismissValue.DismissedToStart -> {
                    toggleQuickReminder(item.id)
                    false
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        snapshotFlow { dismissState.dismissDirection }
            .collect { dismissDirection ->
                willDismissDirection = dismissDirection
            }
    }

    NoFlingSwipeToDismiss(
        state = dismissState,
        background = { SwipeBackground(dismissState, item.hasReminder) },
        dismissContent = content
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBackground(
    dismissState: NoFlingDismissState,
    hasReminder: Boolean,
) {
    val direction = dismissState.dismissDirection ?: return

    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5F)
            DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.primary
            DismissValue.DismissedToStart -> MaterialTheme.colorScheme.primary
        }, label = "Color animation"
    )

    val alignment = when (direction) {
        DismissDirection.StartToEnd -> Alignment.CenterStart
        DismissDirection.EndToStart -> Alignment.CenterEnd
    }

    val icon = when (direction) {
        DismissDirection.StartToEnd -> Icons.Default.Tune
        DismissDirection.EndToStart -> if (hasReminder) Icons.Default.NotificationsOff else Icons.Default.Notifications
    }

    val scale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f,
        label = "Scale animation"
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 20.dp),
        contentAlignment = alignment
    ) {
        Icon(
            icon,
            contentDescription = "Localized description",
            modifier = Modifier.scale(scale)
        )
    }
}
package co.softov.morestuff.android.ui.schedule

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.ui.MaterialColors
import co.softov.morestuff.android.app.ui.get
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityContent(
    snackBarHostState: SnackbarHostState,
    showTaskChat: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
    onItemDragging: (Boolean) -> Unit = {},
    listState: LazyListState = rememberLazyListState(),
    viewModel: PriorityViewModel = koinViewModel(),
) {

    val state = rememberReorderableLazyListState(
        listState = listState,
        onMove = { from, to ->
            viewModel.updateTaskOrder(from.index, to.index)
        },
        onDragEnd = { start, end ->
            viewModel.reorderTaskItem(start, end)
        }
    )

    val resources = LocalContext.current.resources
    LaunchedEffect(viewModel.notification) {
        when (viewModel.notification) {
            NotificationState.Complete -> {
                snackBarHostState.showSnackbar(
                    message = resources.getString(R.string.snack_task_completed),
                    actionLabel = resources.getString(R.string.undo),
                    duration = SnackbarDuration.Long
                ).also {
                    when (it) {
                        SnackbarResult.Dismissed -> viewModel.resetNotification()
                        SnackbarResult.ActionPerformed -> viewModel.undoLastCompleted()
                    }
                }
            }

            else -> {}
        }
    }

    Box(modifier) {
        LazyColumn(
            state = state.listState,
            modifier = modifier
                .fillMaxSize()
                .reorderable(state),
            contentPadding = PaddingValues(bottom = 130.dp),
            horizontalAlignment = Alignment.End
        ) {
            items(viewModel.tasks, key = { it.id }) { task ->
                val item by rememberUpdatedState(task)

                val dismissState = rememberDismissState(
                    positionalThreshold = { 80.dp.toPx() },
                    confirmValueChange = { dismissValue ->
                        when (dismissValue) {
                            DismissValue.Default -> false
                            DismissValue.DismissedToEnd -> {
                                viewModel.completeTask(item)
                                true
                            }

                            DismissValue.DismissedToStart -> {
                                viewModel.toggleReminder(item)
                                false
                            }
                        }
                    }
                )

                var willDismissDirection: DismissDirection? by remember {
                    mutableStateOf(null)
                }

                LaunchedEffect(key1 = Unit, block = {
                    snapshotFlow { dismissState.dismissDirection }
                        .collect { dismissDirection ->
                            willDismissDirection = dismissDirection
                        }
                })

                val haptic = LocalHapticFeedback.current
                LaunchedEffect(key1 = willDismissDirection, block = {
                    if (willDismissDirection != null) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                })

                ReorderableItem(state, key = item.id) { isDragging ->
                    SwipeToDismiss(
                        state = dismissState,
                        background = { SwipeBackground(dismissState) },
                        dismissContent = {
                            PriorityItem(
                                task = item,
                                onClick = showTaskChat,
                                if (!item.hasSchedule) Modifier.detectReorderAfterLongPress(state) else Modifier,
                                isDragging = isDragging
                            )
                        }

                    )
                    AnimatedVisibility(
                        visible = !isDragging,
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Divider(
                            thickness = 0.7.dp,
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )
                    }

                    LaunchedEffect(key1 = isDragging) {
                        onItemDragging(isDragging)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeBackground(dismissState: DismissState) {
    val direction = dismissState.dismissDirection ?: return

    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> MaterialColors.BlueGrey[500]
            DismissValue.DismissedToEnd -> MaterialColors.Green[500]
            DismissValue.DismissedToStart -> MaterialColors.Blue[700]
        }, label = "Color animation"
    )
    val alignment = when (direction) {
        DismissDirection.StartToEnd -> Alignment.CenterStart
        DismissDirection.EndToStart -> Alignment.CenterEnd
    }
    val icon = when (direction) {
        DismissDirection.StartToEnd -> Icons.Default.Done
        DismissDirection.EndToStart -> Icons.Default.Archive
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
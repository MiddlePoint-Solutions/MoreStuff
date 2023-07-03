package co.softov.morestuff.android.ui.schedule

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDismissState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.ui.MaterialColors
import co.softov.morestuff.android.app.ui.get
import co.softov.morestuff.android.domain.model.TaskDomain
import kotlinx.coroutines.launch
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

    val scope = rememberCoroutineScope()
    var taskOptions by remember { mutableStateOf<TaskDomain?>(null) }

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

    taskOptions?.let {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = {
                taskOptions = null
            },
            sheetState = sheetState,
        ) {

            TextButton(
                onClick = {
                    scope.launch {
                        viewModel.completeTask(it)
                        sheetState.hide()
                        taskOptions = null
                    }
                },
                modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        scope.launch {
                            viewModel.completeTask(it)
                            sheetState.hide()
                            taskOptions = null
                        }
                    },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = stringResource(R.string.cd_complete_task)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = stringResource(R.string.complete),
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(120.dp))
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
                    positionalThreshold = { 120.dp.toPx() },
                    confirmValueChange = { dismissValue ->
                        when (dismissValue) {
                            DismissValue.Default -> false
                            DismissValue.DismissedToEnd -> {
                                taskOptions = item
//                                viewModel.completeTask(item)
                                false
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
                        background = { SwipeBackground(dismissState, item.hasReminder) },
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
private fun SwipeBackground(
    dismissState: DismissState,
    hasReminder: Boolean
) {
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
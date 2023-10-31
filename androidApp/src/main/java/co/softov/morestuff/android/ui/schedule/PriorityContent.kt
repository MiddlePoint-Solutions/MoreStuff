package co.softov.morestuff.android.ui.schedule

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.ui.compose.NoFlingDismissState
import co.softov.morestuff.android.ui.compose.NoFlingSwipeToDismiss
import co.softov.morestuff.android.ui.compose.rememberNoFlingDismissState
import co.softov.morestuff.android.ui.utils.explode
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.PartySystem
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityContent(
    snackBarHostState: SnackbarHostState,
    showTaskChat: (taskId: Long) -> Unit,
    onCallToAction: () -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    viewModel: PriorityViewModel = koinViewModel(),
    itemSelectedState: MutableState<Boolean>,
) {
    val scope = rememberCoroutineScope()
    val model by viewModel.model.collectAsState()

    var taskOptions by remember { mutableStateOf<TaskDomain?>(null) }
    var showTaskCompleteAnimation by remember { mutableLongStateOf(0) }
    val showEmptyState by remember { derivedStateOf { viewModel.tasks.isEmpty() } }
    val isBulkMode by derivedStateOf { viewModel.selectedTaskIds.value.isNotEmpty() }

    LaunchedEffect(viewModel.undoBulkMode.collectAsState().value) {
        if (viewModel.undoBulkMode.value) {
            viewModel.selectedTaskIds.value = listOf()
            itemSelectedState.value = false
            viewModel.undoBulkMode.value = false
        }
    }

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

    taskOptions?.let { task ->
        val sheetState = rememberModalBottomSheetState()
        val dismissDialog = { taskOptions = null }
        TaskOptionsDialog(
            sheetState = sheetState,
            task = task,
            dismissDialog = dismissDialog,
            completeTask = {
                showTaskCompleteAnimation = task.id
                scope.launch {
                    viewModel.completeTask(task)
                    sheetState.hide()
                    dismissDialog()
                }
            },
            moveToTop = {
                scope.launch {
                    viewModel.moveToTop(task)
                    sheetState.hide()
                    dismissDialog()
                }
            },
            moveToBottom = {
                scope.launch {
                    viewModel.moveToBottom(task)
                    sheetState.hide()
                    dismissDialog()
                }
            }
        )
    }

    val haptic = LocalHapticFeedback.current

    var willDismissDirection: DismissDirection? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(willDismissDirection) {
        if (willDismissDirection != null) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }




    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(
                items = viewModel.tasks,
                key = { _, task -> task.id }
            ) { index, item ->
                val isLast by remember(index) {
                    derivedStateOf { index == viewModel.tasks.lastIndex }
                }

                val dismissState = rememberNoFlingDismissState(
                    positionalThreshold = { 130.dp.toPx() },
                    confirmValueChange = { dismissValue ->
                        when (dismissValue) {
                            DismissValue.Default -> false
                            DismissValue.DismissedToEnd -> {
                                taskOptions = item
                                false
                            }

                            DismissValue.DismissedToStart -> {
                                viewModel.toggleReminder(item)
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
                    dismissContent = {
                        PriorityItem(
                            task = item,
                            onClick = {
                                if (isBulkMode) {
                                    viewModel.handleTaskSelection(item)
                                    itemSelectedState.value =
                                        viewModel.selectedTaskIds.value.isNotEmpty()
                                } else {
                                    showTaskChat(item.id)
                                }
                            },
                            onLongClick = {
                                viewModel.handleLongPressOnTask(item)
                                itemSelectedState.value = true
                            },
                            isSelected = item.id in viewModel.selectedTaskIds.value
                        )
                    }
                )

                if (!isLast) {
                    Divider(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        thickness = Dp.Hairline
                    )
                }
            }
        }
    }

    if (showTaskCompleteAnimation > 0 && model.enableConfetti) {
        KonfettiView(
            modifier = Modifier.fillMaxSize(),
            parties = explode(),
            updateListener = object : OnParticleSystemUpdateListener {
                override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int) {
                    if (activeSystems == 0) showTaskCompleteAnimation = 0
                }
            }
        )
    }

    if (showEmptyState) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 180.dp),
            contentAlignment = Alignment.Center
        ) {
            TextButton(onClick = onCallToAction) {
                Text(
                    stringResource(R.string.empty_priority_list_cta),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
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
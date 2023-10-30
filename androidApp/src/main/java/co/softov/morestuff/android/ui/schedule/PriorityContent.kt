package co.softov.morestuff.android.ui.schedule

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.ui.chat.Visibility
import co.softov.morestuff.android.ui.compose.NoFlingDismissState
import co.softov.morestuff.android.ui.compose.NoFlingSwipeToDismiss
import co.softov.morestuff.android.ui.compose.rememberNoFlingDismissState
import co.softov.morestuff.android.ui.utils.explode
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.PartySystem
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.koin.androidx.compose.koinViewModel

private val jumpToTopThreshold = 56.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityContent(
    snackBarHostState: SnackbarHostState,
    showTaskChat: (taskId: Long) -> Unit,
    onCallToAction: () -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    viewModel: PriorityViewModel = koinViewModel(),
) {

    val scope = rememberCoroutineScope()
    val model by viewModel.model.collectAsState()

    var taskOptions by remember { mutableStateOf<TaskDomain?>(null) }
    var showTaskCompleteAnimation by remember { mutableLongStateOf(0) }
    val showEmptyState by remember { derivedStateOf { viewModel.tasks.isEmpty() } }

    val state = rememberReorderableLazyListState(
        listState = listState,
        onMove = { from, to ->
            viewModel.updateTaskOrder(from.index, to.index)
        },
        onDragEnd = { start, end ->
            viewModel.reorderTaskItem(start, end)
        }
    )
    val scrollState = rememberLazyListState()

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

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .reorderable(state),
        ) {

            itemsIndexed(
                items = viewModel.tasks,
                key = { _, task -> task.id }
            ) { index, item ->
                val task by rememberUpdatedState(item)
                val itemClick by rememberUpdatedState(showTaskChat)

                val isLast by remember(index) {
                    derivedStateOf { index == viewModel.tasks.lastIndex }
                }

                val dismissState = rememberNoFlingDismissState(
                    positionalThreshold = { 130.dp.toPx() },
                    confirmValueChange = { dismissValue ->
                        when (dismissValue) {
                            DismissValue.Default -> false
                            DismissValue.DismissedToEnd -> {
                                taskOptions = task
                                false
                            }

                            DismissValue.DismissedToStart -> {
                                viewModel.toggleReminder(task)
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

                ReorderableItem(state, key = task.id) { isDragging ->
                    NoFlingSwipeToDismiss(
                        state = dismissState,
                        background = { SwipeBackground(dismissState, task.hasReminder) },
                        dismissContent = {
                            PriorityItem(
                                task = task,
                                onClick = itemClick,
                                if (!task.hasSchedule) Modifier.detectReorderAfterLongPress(state) else Modifier,
                                isDragging = isDragging
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
        val jumpThreshold = with(LocalDensity.current) {
            jumpToTopThreshold.toPx()
        }
        val jumpToTopButtonEnabled by remember {
            derivedStateOf {
                (scrollState.firstVisibleItemIndex != 0 ||
                        scrollState.firstVisibleItemScrollOffset > jumpThreshold)
            }
        }
        JumpToTop(
            enabled = jumpToTopButtonEnabled,
            onClicked = {
                scope.launch {

                    scrollState.scrollToItem(0)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 40.dp)
        )


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
                TextButton(
                    onClick = onCallToAction,
                ) {
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

@Composable
fun JumpToTop(
    enabled: Boolean,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transition = updateTransition(
        if (enabled) Visibility.VISIBLE else Visibility.GONE,
        label = "JumpToTopTransition"
    )
    val topOffset by transition.animateDp(label = "JumpToTopTransition") {
        if (it == Visibility.GONE) {
            (-32).dp
        } else {
            32.dp
        }
    }
    if (topOffset > 0.dp) {
        ExtendedFloatingActionButton(
            icon = {
                Icon(
                    imageVector = Icons.Filled.ArrowUpward,
                    modifier = Modifier.height(18.dp),
                    contentDescription = null
                )
            },
            text = {
                Text(text = stringResource(id = R.string.jump_to_top))
            },
            onClick = onClicked,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = modifier
                .offset(x = 0.dp, y = topOffset)
                .height(36.dp)
        )
    }
}

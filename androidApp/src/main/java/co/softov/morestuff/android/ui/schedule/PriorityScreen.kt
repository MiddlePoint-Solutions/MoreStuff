package co.softov.morestuff.android.ui.schedule

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityScreen(
    viewModel: PriorityViewModel = koinViewModel()
) {

    val model by viewModel.uiModel.collectAsState()

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text(text = "Priority") }
        )
    }) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Box {
                PrioritySchedule(
                    items = model.items,
                    reorderAction = viewModel::reorderTaskItem,
                    completeAction = viewModel::completeTask
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrioritySchedule(
    items: List<ScheduleListItemViewModel>,
    reorderAction: (Int, Int) -> Unit,
    completeAction: (ScheduleListItemViewModel) -> Unit,
    modifier: Modifier = Modifier,
) {

    val state = rememberReorderableLazyListState(
        onMove = { to, from ->
            reorderAction(to.index, from.index)
        }
    )

    LazyColumn(
        state = state.listState,
        modifier = modifier
            .fillMaxSize()
            .reorderable(state),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(items, key = { it.taskId }) {

            val item by rememberUpdatedState(it)
            val dismissState = rememberDismissState(
                confirmValueChange = { dismissValue ->
                    if (dismissValue == DismissValue.DismissedToEnd) {
                        completeAction(item)
                        true
                    } else false
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

            ReorderableItem(state, key = it.taskId) { isDragging ->
                SwipeToDismiss(
                    state = dismissState,
                    background = { SwipeBackground(dismissState) },
                    dismissContent = {
                        ScheduleItem(
                            it,
                            Modifier.detectReorderAfterLongPress(state),
                            isDragging = isDragging
                        )
                    })
            }


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleItem(
    task: ScheduleListItemViewModel,
    modifier: Modifier = Modifier,
    isDragging: Boolean = false,
    itemAction: () -> Unit = {},
) {

    val elevation = CardDefaults.cardElevation(
        defaultElevation = animateDpAsState(if (isDragging) 12.dp else 0.dp).value
    )

    Card(
        onClick = itemAction,
        elevation = elevation,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(56.dp)
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = task.taskTitle,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeBackground(dismissState: DismissState) {
    val direction = dismissState.dismissDirection ?: return

    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> Color.LightGray
            DismissValue.DismissedToEnd -> Color.Green
            DismissValue.DismissedToStart -> Color.Red
        }
    )
    val alignment = when (direction) {
        DismissDirection.StartToEnd -> Alignment.CenterStart
        DismissDirection.EndToStart -> Alignment.CenterEnd
    }
    val icon = when (direction) {
        DismissDirection.StartToEnd -> Icons.Default.Done
        DismissDirection.EndToStart -> Icons.Default.Delete
    }
    val scale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
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
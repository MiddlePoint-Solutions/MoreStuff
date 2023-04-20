package co.softov.morestuff.android.ui.review

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.BuildConfig
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.util.rememberRandomColor
import co.softov.morestuff.android.presentation.presenter.ReviewModel
import co.softov.morestuff.android.presentation.presenter.ReviewRound
import co.softov.morestuff.android.ui.list.ScheduleListItem
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel
import co.softov.morestuff.android.ui.priority.PriorityButton
import co.softov.morestuff.android.ui.review.swipeable.*
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ReviewContent(
    modifier: Modifier = Modifier,
    viewModel: ReviewViewModel = koinViewModel()
) {
    Surface {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xfff68084),
                            Color(0xffa6c0fe),
                        )
                    )
                )
                .systemBarsPadding()
        ) {

            val model by viewModel.uiModel.collectAsState()
            val scope = rememberCoroutineScope()

            Column {
                PriorityReviewTopBar(navigateUp = viewModel::navigateBack)
                RoundInfo(model)
            }

            Box {

                val states =
                    model.roundItems.map { it to rememberSwipeableCardState(model.roundNumber) }

                val undoAction: () -> Unit = remember(model.roundNumber) {
                    {
                        scope.launch {
                            states.run {
                                firstVisibleOrNull()?.let { first ->
                                    getOrNull(indexOf(first) + 1)?.let {
                                        it.second.undo()
                                        viewModel.undoTask(it.first)
                                    }
                                }
                            }
                        }
                    }
                }

                val negativeAction: () -> Unit = remember(Unit) {
                    {
                        scope.launch {
                            states.firstVisibleStateOrNull()?.swipe(SwipeDirection.Left)
                        }
                    }
                }

                val positiveAction: () -> Unit = remember(Unit) {
                    {
                        scope.launch {
                            states.firstVisibleStateOrNull()?.swipe(SwipeDirection.Right)
                        }
                    }
                }

                val doneAction: () -> Unit = remember(Unit) {
                    {
                        scope.launch {
                            states.firstVisibleStateOrNull()?.swipe(SwipeDirection.Up)
                        }
                    }
                }

                val laterAction: () -> Unit = remember(Unit) {
                    {
                        scope.launch {
                            states.firstVisibleStateOrNull()?.swipe(SwipeDirection.Down)
                        }
                    }
                }

                when (model.round) {

                    ReviewRound.Priority -> {

                        val visibleState = remember(model.roundNumber) {
                            MutableTransitionState(false)
                        }

                        TaskPrioritySwipe(
                            modifier = modifier.align(Alignment.Center),
                            states = states,
                            onSwiped = { schedule, direction, isLast ->
                                scope.launch {
                                    if (isLast) {
                                        visibleState.targetState = false
                                        delay(100)
                                    }
                                }.invokeOnCompletion {
                                    viewModel.onTaskSwiped(schedule, direction, isLast)
                                }
                            },
                        )

                        SlideAnimation(
                            visibleState = visibleState,
                            key1 = model.roundNumber,
                            modifier = modifier.align(Alignment.BottomCenter),
                        ) {
                            ReviewSwipeControls(
                                modifier = modifier.align(Alignment.BottomCenter),
                                negativeAction = negativeAction,
                                positiveAction = positiveAction,
                                doneAction = doneAction,
                                laterAction = laterAction,
                                undoAction = undoAction,
                            )
                        }
                    }
//                    ReviewRound.Sort -> {
//
//                        val visibleState = remember(model.roundNumber) {
//                            MutableTransitionState(false)
//                        }
//                        val transition = updateTransition(visibleState, "Visible state")
//
//                        val screenWidth = with(LocalDensity.current) {
//                            LocalConfiguration.current.screenWidthDp.dp.toPx()
//                        }
//
//                        val xPosition by transition.animateFloat(label = "xPosition") {
//                            if (it) 0f else screenWidth
//                        }
//
//                        Box(modifier = Modifier.graphicsLayer {
//                            translationX = xPosition
//                        }) {
//                            TaskPrioritySwipe(
//                                modifier = modifier
//                                    .align(Alignment.Center)
//                                    .layoutId("${model.round}-${model.roundNumber}"),
//                                states = states,
//                                onSwiped = { schedule, direction, isLast ->
//                                    scope.launch {
//                                        if (isLast) {
//                                            visibleState.targetState = false
//                                            delay(100)
//                                        }
//                                    }.invokeOnCompletion {
//                                        viewModel.onTaskSwiped(schedule, direction, isLast)
//                                    }
//                                },
//                            )
//                        }
//
//                        SlideAnimation(
//                            visibleState = visibleState,
//                            key1 = model.roundNumber,
//                            modifier = modifier.align(Alignment.BottomCenter),
//                        ) {
//                            ReviewSwipeControls(
//                                modifier = modifier.align(Alignment.BottomCenter),
//                                negativeAction = negativeAction,
//                                positiveAction = positiveAction,
//                                doneAction = doneAction,
//                                laterAction = laterAction,
//                                undoAction = undoAction,
//                            )
//                        }
//                    }
                    ReviewRound.Final -> {

                        val visibleState = remember(model.roundNumber) {
                            MutableTransitionState(false)
                        }
                        val transition = updateTransition(visibleState, "Visible state")

                        val screenWidth = with(LocalDensity.current) {
                            LocalConfiguration.current.screenWidthDp.dp.toPx()
                        }

                        val xPosition by transition.animateFloat(label = "xPosition") {
                            if (it) 0f else screenWidth
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxSize()
                                .aspectRatio(1f)
                                .graphicsLayer {
                                    translationX = xPosition
                                }
                        ) {
                            when {
                                states.isEmpty() -> {
                                    Text(
                                        "Wooops, nothing to work on? add a new task",
                                        modifier.align(Alignment.Center)
                                    )
                                }
                                states.size == 1 -> {
                                    Box(
                                        modifier
                                            .padding(24.dp)
                                            .fillMaxSize()
                                            .aspectRatio(1f)
                                    ) {
                                        TaskCard(
                                            modifier = modifier.fillMaxSize(),
                                            schedule = states.first().first
                                        )
                                    }
                                }
                                else -> {
                                    LazyColumn(
                                        contentPadding = PaddingValues(8.dp)
                                    ) {
                                        items(model.roundItems) {
                                            ScheduleListItem(it)
                                        }
                                    }
                                }
                            }
                        }

                        SlideAnimation(
                            visibleState = visibleState,
                            key1 = model.roundNumber,
                            modifier = modifier.align(Alignment.BottomCenter),
                        ) {
                            ReviewFinalControls(
                                modifier = modifier.align(Alignment.BottomCenter),
                                resetAction = viewModel::reset,
                                finishAction = viewModel::confirmResults
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun List<Pair<ScheduleListItemViewModel, SwipeableCardState>>.firstVisibleOrNull() =
    reversed()
        .firstOrNull {
            it.second.offset.value == Offset(0f, 0f)
        }

private fun List<Pair<ScheduleListItemViewModel, SwipeableCardState>>.firstVisibleStateOrNull() =
    firstVisibleOrNull()?.second

@Composable
private fun SlideAnimation(
    visibleState: MutableTransitionState<Boolean>,
    key1: Any?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visibleState = visibleState,
        modifier = modifier,
        enter = slideInVertically { it * 2 },
        exit = slideOutVertically { it * 2 }
    ) {
        content()
    }

    LaunchedEffect(key1 = key1) {
        visibleState.targetState = true
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PriorityReviewTopBar(
    navigateUp: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.priority_review),
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_navigate_back)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun ReviewFinalControls(
    modifier: Modifier = Modifier,
    resetAction: () -> Unit = {},
    finishAction: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(Modifier.align(Alignment.CenterHorizontally)) {
            CircleButton(
                onClick = resetAction,
                icon = Icons.Rounded.Replay
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        PriorityButton(
            onSelected = finishAction,
            text = stringResource(R.string.start).uppercase(),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(16.dp),
            fontSize = 26.sp,
            shape = RoundedCornerShape(50)
        )

    }
}

@Composable
private fun ReviewSwipeControls(
    modifier: Modifier = Modifier,
    positiveAction: () -> Unit = {},
    negativeAction: () -> Unit = {},
    doneAction: () -> Unit = {},
    laterAction: () -> Unit = {},
    undoAction: () -> Unit = {},
) {
    Column(
        modifier = modifier
    ) {

        Box(Modifier.align(Alignment.CenterHorizontally)) {
            CircleButton(
                onClick = undoAction,
                icon = Icons.Rounded.Undo
            )
        }

        Row(
            Modifier
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CircleButton(
                onClick = negativeAction,
                icon = Icons.Rounded.ThumbDown
            )
            CircleButton(
                onClick = laterAction,
                icon = Icons.Rounded.Close
            )
            CircleButton(
                onClick = doneAction,
                icon = Icons.Rounded.Done
            )
            CircleButton(
                onClick = positiveAction,
                icon = Icons.Rounded.ThumbUp
            )
        }
    }
}

@Composable
@OptIn(ExperimentalSwipeableCardApi::class)
private fun TaskPrioritySwipe(
    modifier: Modifier,
    states: List<Pair<ScheduleListItemViewModel, SwipeableCardState>>,
    onSwiped: (schedule: ScheduleListItemViewModel, direction: SwipeDirection, isLast: Boolean) -> Unit,
) {

    Box(
        modifier
            .padding(24.dp)
            .fillMaxSize()
    ) {
        states.forEachIndexed { index, (schedule, state) ->
            if (state.swipedDirection == null) {

                val selectedState = remember(schedule.taskId) { MutableTransitionState(false) }
                val selectedTransition = updateTransition(selectedState, "Selected Transition")
                val ratio by selectedTransition.animateFloat(label = "AspectRatio") {
                    if (it) 0.8f else 1f
                }

                TaskCard(
                    modifier = modifier
                        .layoutId(schedule.taskId)
                        .fillMaxSize()
                        .aspectRatio(ratio)
                        .swipableCard(state = state)
                        .graphicsLayer {
                            translationY = -(5 * index).dp.toPx()
                        }
                        .clickable {
                            selectedState.targetState = !selectedState.currentState
                        },
                    schedule = schedule
                )
            }
            LaunchedEffect(schedule, state.swipedDirection) {
                state.swipedDirection?.let {
                    onSwiped(schedule, it, states.first().first == schedule)
                }
            }
        }
    }
}

@Composable
private fun CircleButton(
    onClick: () -> Unit,
    icon: ImageVector,
) {
    IconButton(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .size(56.dp)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
        onClick = onClick
    ) {
        Icon(
            icon, null,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun TaskCard(
    modifier: Modifier = Modifier,
    schedule: ScheduleListItemViewModel,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            rememberRandomColor(),
                            rememberRandomColor(),
                        )
                    )
                )
        ) {
            Column(Modifier.align(Alignment.Center)) {
                Text(
                    text = schedule.taskTitle,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
private fun RoundInfo(
    model: ReviewModel,
    modifier: Modifier = Modifier
) {

    val info by remember(model.roundNumber) {
        mutableStateOf("Round ${model.roundNumber} - ${model.round}")
    }
    val instructions by remember(model.round) {
        when (model.round) {
            ReviewRound.Priority -> "<-- Low    High -->"
            ReviewRound.Final -> ""
        }.let {
            mutableStateOf(it)
        }
    }

    Column(
        modifier = modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = info,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = instructions,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )

        if (BuildConfig.DEBUG) {

            val total by remember {
                mutableStateOf("Total: ${model.roundItems.size}")
            }

            Text(
                text = total,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )

            val debug = with(model) {
                "RoundItems: ${roundItems.size}  Tomorrow: ${tomorrow.size} High: ${high.size}  Low: ${low.size}  Done: ${done.size}"
            }

            Text(
                text = debug,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Preview
@Composable
fun TaskCardPreview() {
    MoreStuffTheme(darkTheme = true) {
        TaskCard(
            modifier = Modifier.aspectRatio(1f),
            schedule = ScheduleListItemViewModel(taskTitle = "Hellooooo there")
        )
    }
}

@Preview
@Composable
fun ReviewSwipeControlsPreview() {
    MoreStuffTheme(darkTheme = true) {
        ReviewSwipeControls()
    }
}

@Preview
@Composable
fun ReviewFinalControlsPreview() {
    MoreStuffTheme(darkTheme = true) {
        ReviewFinalControls()
    }
}
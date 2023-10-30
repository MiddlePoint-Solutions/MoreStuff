package co.softov.morestuff.android.ui.review

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.findViewTreeLifecycleOwner
import co.softov.morestuff.android.R
import co.softov.morestuff.android.presentation.presenter.ReviewRound
import co.softov.morestuff.android.ui.chat.task.TaskChatScreen
import co.softov.morestuff.android.ui.compose.ProvideLocalViewModelStoreOwner
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import co.softov.morestuff.android.ui.model.ReviewItemUiModel
import co.softov.morestuff.android.ui.onboarding.DefaultHintTask
import co.softov.morestuff.android.ui.onboarding.ReviewCardsOnBoarding
import co.softov.morestuff.android.ui.review.swipeable.*
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.reviewIconTint
import co.softov.morestuff.android.ui.theme.surfaceContainer
import com.arkivanov.decompose.router.stack.pop
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun ReviewScreen(
    modifier: Modifier = Modifier,
) {
    val navigation = LocalAppNavigation.current
    val lifecycleOwner = LocalView.current.findViewTreeLifecycleOwner()
    ProvideLocalViewModelStoreOwner(lifecycleOwner) {
        ReviewContent(
            onBack = navigation::pop,
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewContent(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReviewViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    var isCardMoving by remember { mutableStateOf(false) }
    var isBottomSheetVisible by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var currentTaskId by rememberSaveable { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {

        val model by viewModel.uiModel.collectAsState()

        BoxWithConstraints(
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {

            when (model.round) {
                ReviewRound.Review -> {

                    AnimatedVisibility(
                        visible = !isCardMoving,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        PriorityReviewTopBar(
                            navigateUp = onBack,
                            disableHintArrow = {
                                scope.launch {
                                    viewModel.toggleHintArrowPriority()
                                }
                            },
                            isHintArrowActive = viewModel.reviewHintEnabled
                        )
                    }

                    val states = model.items.map { it to rememberSwipeableCardState(model.round) }

                    val visibleState = remember(model.round) { MutableTransitionState(false) }

                    LaunchedEffect(isCardMoving) {
                        visibleState.targetState = !isCardMoving
                    }
                    AnimatedVisibility(
                        visibleState = visibleState,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = modifier
                            .fillMaxHeight(0.30f)
                            .align(Alignment.BottomCenter),
                    ) {
                        ReviewSwipeControls(
                            lastItemSwiped = { states.lastSwipedItem() },
                            firstVisibleState = { states.firstVisibleStateOrNull() },
                            undoAction = viewModel::undo,
                        )
                    }

                    TaskPrioritySwipe(
                        modifier = modifier
                            .offset(y = 64.dp)
                            .fillMaxHeight(0.7f),
                        states = states,
                        onSwiped = { schedule, direction ->
                            viewModel.onTaskSwiped(schedule, direction)
                            isCardMoving = false
                        },
                        onComplete = {
                            scope.launch {
                                states.firstVisibleStateOrNull()?.onComplete()
                                viewModel.completeTask(it)
                            }
                        },
                        onCardMoveStateChange = { moving -> isCardMoving = moving },
                        showTaskChat = { taskId ->
                            currentTaskId = taskId
                            scope.launch {
                                isBottomSheetVisible = true
                            }
                        }
                    )

                    AnimatedVisibility(
                        visible = isCardMoving && viewModel.reviewHintEnabled,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        ReviewDragHint()
                    }

                    if (isBottomSheetVisible) {
                        BackHandler(onBack = {
                            scope.launch {
                                bottomSheetState.hide()
                            }
                        })
                        ModalBottomSheet(
                            sheetState = bottomSheetState,
                            onDismissRequest = {
                                isBottomSheetVisible = false
                            }
                        ) {
                            currentTaskId?.let { taskId ->
                                TaskChatScreen(
                                    taskId = taskId,
                                    onBack,
                                    shouldShowAppBar = { false })
                            }
                        }
                    }
                    LaunchedEffect(key1 = model.round) {
                        visibleState.targetState = true
                    }
                }

                ReviewRound.Final -> {
                    LaunchedEffect(Unit) {
                        onBack()
                    }
                }
            }
        }
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PriorityReviewTopBar(
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {},
    disableHintArrow: () -> Unit,
    isHintArrowActive: Boolean,
) {
    var isBottomSheetVisible by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val tasks = DefaultHintTask.tasks.map { task ->
        task.copy(title = stringResource(task.title.toInt()))
    }
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.priority_review),
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
        actions = {
            IconButton(onClick = { isBottomSheetVisible = true }) {
                Icon(
                    imageVector = Icons.Default.Help,
                    contentDescription = stringResource(R.string.help)
                )
            }
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.show_hint_arrow_priority)
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(onClick = {
                    scope.launch {
                        disableHintArrow()
                        showMenu = false
                    }
                },
                    text = {
                        Text(
                            text = if (isHintArrowActive)
                                stringResource(R.string.disable_hint_arrow)
                            else stringResource(R.string.enable_hint_arrow)
                        )
                    })

            }

        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )


    if (isBottomSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { isBottomSheetVisible = false },
            sheetState = bottomSheetState,
            content = {
                var reloadCards by remember { mutableIntStateOf(0) }
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(9000)
                        reloadCards++
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 70.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    key(reloadCards) {
                        ReviewCardsOnBoarding(tasks)
                    }
                }
            }
        )
    }
}


@Composable
private fun ReviewSwipeControls(
    lastItemSwiped: () -> Pair<ReviewItemUiModel, SwipeableCardState>?,
    firstVisibleState: () -> SwipeableCardState?,
    undoAction: (ReviewItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {

    val scope = rememberCoroutineScope()

    val undoLastAction: () -> Unit = {
        scope.launch {
            Timber.d("Undoing")
            lastItemSwiped()?.let { lastItem ->
                lastItem.second.undo()
                undoAction(lastItem.first)
            }
        }
    }

    val lowAction: () -> Unit = {
        scope.launch {
            firstVisibleState()?.swipe(SwipeDirection.Left)
        }
    }

    val highAction: () -> Unit = {
        scope.launch {
            firstVisibleState()?.swipe(SwipeDirection.Right)
        }
    }

    val doneAction: () -> Unit = {
        scope.launch {
            firstVisibleState()?.swipe(SwipeDirection.Up)
        }
    }

    val laterAction: () -> Unit = {
        scope.launch {
            firstVisibleState()?.swipe(SwipeDirection.Down)
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(11.dp, Alignment.CenterHorizontally)
        ) {
            MainReviewButton(
                onClick = lowAction,
                icon = Icons.Rounded.Remove

            )
            MainReviewButton(
                onClick = highAction,
                icon = Icons.Rounded.Add
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(11.dp, Alignment.CenterHorizontally)
        ) {

            SecondaryReviewButton(
                onClick = laterAction,
                icon = ImageVector.vectorResource(id = R.drawable.ic_review_later_24px)
            )

            SecondaryReviewButton(
                onClick = undoLastAction,
                icon = ImageVector.vectorResource(id = R.drawable.ic_review_undo_24px)
            )

            SecondaryReviewButton(
                onClick = doneAction,
                icon = ImageVector.vectorResource(id = R.drawable.ic_review_now_24px)
            )


        }
    }
}

@Composable
@OptIn(ExperimentalSwipeableCardApi::class)
private fun TaskPrioritySwipe(
    modifier: Modifier = Modifier,
    states: List<Pair<ReviewItemUiModel, SwipeableCardState>>,
    onSwiped: (schedule: ReviewItemUiModel, direction: SwipeDirection) -> Unit,
    onComplete: (ReviewItemUiModel) -> Unit,
    onCardMoveStateChange: (Boolean) -> Unit,
    showTaskChat: (taskId: Long) -> Unit,
) {
    val itemClick by rememberUpdatedState(showTaskChat)
    Box(
        modifier = modifier.padding(20.dp)
    ) {
        states.forEach { (task, state) ->

            val isMoving by remember(state.offset) {
                derivedStateOf { state.offset.value.x != 0f || state.offset.value.y != 0f }
            }

            LaunchedEffect(isMoving) {
                onCardMoveStateChange(isMoving)
            }

            if (state.swipedDirection == null) {
                val isVisible = state == states.firstVisibleOrNull()?.second

                TaskCard(
                    modifier = Modifier
                        .layoutId(task.id)
                        .swipableCard(state = state),
                    task = task,
                    onComplete = onComplete,
                    isVisible = isVisible,
                    showTaskChat = itemClick
                )
            }
            LaunchedEffect(task, state.swipedDirection) {
                state.swipedDirection?.let { direction ->
                    onSwiped(task, direction)
                }
            }
        }
    }
}

@Composable
private fun ReviewDragHint() {
    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(15)
                ).padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_up),
                contentDescription = stringResource(R.string.cd_highest_priority_hint_icon),
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.width(8.dp))
            ReviewHintTitle(text = stringResource(R.string.review_hint_highest_priority))
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(15)
                ).padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ReviewHintTitle(text = stringResource(R.string.review_hint_lowest_priority))
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_lowest),
                contentDescription = stringResource(R.string.cd_lowest_priority_hint_icon),
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(15)
                ).padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_high),
                contentDescription = stringResource(R.string.cd_high_priority_hint_icon),
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.width(8.dp))
            ReviewHintTitle(text = stringResource(R.string.review_hint_high_priority))
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(15)
                ).padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_low),
                contentDescription = stringResource(R.string.cd_low_priority_hint_icon),
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.width(8.dp))
            ReviewHintTitle(text = stringResource(R.string.review_hint_low_priority))
        }
    }
}

@Composable
private fun ReviewHintTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall.copy(
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
    )
}

@Composable
private fun MainReviewButton(
    onClick: () -> Unit,
    icon: ImageVector,
) {
    Button(
        modifier = Modifier
            .size(width = 140.dp, height = 56.dp)
            .clip(CircleShape),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        onClick = onClick
    ) {
        Icon(
            icon,
            null,
            modifier = Modifier.size(32.dp),
            tint = reviewIconTint
        )
    }
}

@Composable
private fun SecondaryReviewButton(
    onClick: () -> Unit,
    icon: ImageVector,
) {

    Button(
        modifier = Modifier
            .size(width = 90.dp, height = 48.dp)
            .clip(CircleShape),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        onClick = onClick
    ) {
        Icon(
            icon,
            null,
            modifier = Modifier.size(24.dp),
            tint = reviewIconTint
        )
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
fun ReviewSwipeControlsPreview() {
    MoreStuffTheme {
        ReviewSwipeControls({ null }, { null }, {})
    }
}

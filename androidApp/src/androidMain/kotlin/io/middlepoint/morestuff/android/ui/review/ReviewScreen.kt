package io.middlepoint.morestuff.android.ui.review

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.arkivanov.decompose.router.stack.pop
import io.middlepoint.morestuff.android.R
import io.middlepoint.morestuff.android.ui.chat.task.TaskChatScreen
import io.middlepoint.morestuff.android.ui.components.ScopeCarousel
import io.middlepoint.morestuff.android.ui.compose.ProvideLocalViewModelStoreOwner
import io.middlepoint.morestuff.android.ui.compose.SlideAnimation
import io.middlepoint.morestuff.android.ui.local.LocalAppRouter
import io.middlepoint.morestuff.shared.ui.model.ReviewItemUiModel
import io.middlepoint.morestuff.shared.ui.screen.onboarding.OnBoardingReviewScreen
import io.middlepoint.morestuff.android.ui.review.swipeable.ExperimentalSwipeableCardApi
import io.middlepoint.morestuff.shared.ui.components.swipeable.SwipeDirection
import io.middlepoint.morestuff.shared.ui.components.swipeable.SwipeableCardState
import io.middlepoint.morestuff.shared.ui.components.swipeable.firstVisibleStateOrNull
import io.middlepoint.morestuff.shared.ui.components.swipeable.lastSwipedItem
import io.middlepoint.morestuff.shared.ui.components.swipeable.rememberSwipeableCardState
import io.middlepoint.morestuff.shared.ui.components.swipeable.swipableCard
import io.middlepoint.morestuff.android.ui.theme.reviewIconTint
import io.middlepoint.morestuff.shared.domain.model.ScopeDomain
import io.middlepoint.morestuff.shared.ui.components.TaskCard
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ReviewScreen(
    modifier: Modifier = Modifier,
    currentScopeId: Long,
) {
    val navigation = LocalAppRouter.current
    val lifecycleOwner = LocalView.current.findViewTreeLifecycleOwner()
    ProvideLocalViewModelStoreOwner(lifecycleOwner) {
        ReviewContent(
            onBack = navigation::pop,
            modifier = modifier,
            currentScopeId = currentScopeId
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewContent(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    currentScopeId: Long,
    viewModel: ReviewViewModel = koinViewModel(),
) {

    val scope = rememberCoroutineScope()
    var showReviewHelpScreen by remember { mutableStateOf(false) }
    val showTaskChat = remember { MutableTransitionState(false) }
    var currentTaskId by rememberSaveable { mutableStateOf<Long?>(null) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showReviewDragHints by remember { mutableStateOf(false) }
    val model by viewModel.models.collectAsState()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {



        ConstraintLayout(
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {

            val (topBar, cards, controls) = createRefs()

            when (model.round) {

                is ReviewRound.Review -> {

                    PriorityReviewTopBar(
                        scopes = model.scopes,
                        navigateUp = onBack,
                        toggleReviewHint = { viewModel.take(ReviewViewEvent.ToggleReviewHint) },
                        showReviewHelpScreen = { showReviewHelpScreen = true },
                        isReviewHintActive = model.reviewHintEnabled,
                        modifier = Modifier.constrainAs(topBar) {
                            top.linkTo(parent.top)
                        },
                        currentScopeId = currentScopeId,
                    )
                    val states = model.items.map { it to rememberSwipeableCardState(model.round) }

                    val visibleState = remember(model.round) { MutableTransitionState(false) }

                    LaunchedEffect(showReviewDragHints) {
                        visibleState.targetState = !showReviewDragHints
                    }

                    AnimatedVisibility(
                        visibleState = visibleState,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = modifier
                            .navigationBarsPadding()
                            .fillMaxWidth()
                            .constrainAs(controls) {
                                top.linkTo(cards.bottom)
                                height = Dimension.preferredWrapContent
                            },
                    ) {
                        ReviewSwipeControls(
                            lastItemSwiped = { states.lastSwipedItem() },
                            firstVisibleState = { states.firstVisibleStateOrNull() },
                            undoAction = { viewModel.take(ReviewViewEvent.Undo(it)) },
                        )
                    }

                    TaskPrioritySwipe(
                        modifier = modifier
                            .fillMaxHeight(0.7f)
                            .constrainAs(cards) {
                                centerVerticallyTo(parent, bias = 0.4f)
                            },
                        states = states,
                        onSwiped = { schedule, direction ->
                            viewModel.take(ReviewViewEvent.ItemSwipe(schedule, direction))
                        },
                        onDrag = { isDragging ->
                            showReviewDragHints = isDragging
                        },
                        onComplete = {
                            scope.launch {
                                states.firstVisibleStateOrNull()?.onComplete()
                                viewModel.take(ReviewViewEvent.CompleteTask(it))
                            }
                        },
                        showTaskChat = { taskId ->
                            currentTaskId = taskId
                            scope.launch {
                                showTaskChat.targetState = true
                            }
                        }
                    )

                    AnimatedVisibility(
                        visible = showReviewDragHints && model.reviewHintEnabled,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        ReviewDragHint()
                    }

                    LaunchedEffect(model.round) {
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


        SlideAnimation(visibleState = showTaskChat) {
            BackHandler(onBack = {
                scope.launch {
                    showTaskChat.targetState = false
                }
            })
            currentTaskId?.let { taskId ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    TaskChatScreen(
                        taskId = taskId,
                        onBack = { showTaskChat.targetState = false },
                    )
                }
            }
        }

        if (showReviewHelpScreen) {
            ModalBottomSheet(
                onDismissRequest = { showReviewHelpScreen = false },
                sheetState = bottomSheetState,
                content = {
                    OnBoardingReviewScreen(
                        nextButtonText = stringResource(R.string.button_close),
                        onNext = {
                            scope.launch {
                                bottomSheetState.hide()
                                showReviewHelpScreen = false
                            }
                        }
                    )
                }
            )
        }
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PriorityReviewTopBar(
    scopes: List<ScopeDomain>,
    toggleReviewHint: () -> Unit,
    showReviewHelpScreen: () -> Unit,
    isReviewHintActive: Boolean,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {},
    currentScopeId: Long,
    viewModel: ReviewViewModel = koinViewModel()
) {

    val coroutineScope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.priority_review),
                )
            },
            navigationIcon = {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_navigate_back)
                    )
                }
            },
            actions = {
                IconButton(onClick = showReviewHelpScreen) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Help,
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
                        coroutineScope.launch {
                            toggleReviewHint()
                            showMenu = false
                        }
                    },
                        text = {
                            Text(
                                text = if (isReviewHintActive) {
                                    stringResource(R.string.disable_hint_arrow)
                                } else {
                                    stringResource(R.string.enable_hint_arrow)
                                }
                            )
                        })

                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        ScopeCarousel(
            scopes = scopes,
            currentScopeId = currentScopeId,
            onScopeSelected = { viewModel.take(ReviewViewEvent.LoadScope(it)) },
            modifier = Modifier
                .height(75.dp)
                .padding(bottom = 30.dp)
                .fillMaxWidth()
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
            lastItemSwiped()?.let { lastItem ->
                lastItem.second.undo()
                undoAction(lastItem.first)
            }
        }
    }
    val buttonAction: (SwipeDirection) -> Unit = { direction ->
        scope.launch {
            firstVisibleState()?.swipe(direction)
        }
    }

    val lowAction: () -> Unit = { buttonAction(SwipeDirection.Left) }
    val highAction: () -> Unit = { buttonAction(SwipeDirection.Right) }
    val doneAction: () -> Unit = { buttonAction(SwipeDirection.Up) }
    val laterAction: () -> Unit = { buttonAction(SwipeDirection.Down) }

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
    onDrag: (Boolean) -> Unit,
    onComplete: (ReviewItemUiModel) -> Unit,
    showTaskChat: (taskId: Long) -> Unit,
) {
    val itemClick by rememberUpdatedState(showTaskChat)
    Box(
        modifier = modifier
            .padding(20.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.onboarding_review_all_done),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 38.sp
                ),
                color = MaterialTheme.colorScheme.secondary
            )
        }

        states.forEach { (task, state) ->
            if (state.swipedDirection == null) {
                TaskCard(
                    modifier = Modifier
                        .layoutId(task.id)
                        .swipableCard(
                            state = state,
                            onDrag = onDrag,
                        ),
                    item = task,
                    onComplete = onComplete,
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
                )
                .padding(10.dp),
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
                )
                .padding(10.dp),
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
                )
                .padding(10.dp),
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
                )
                .padding(10.dp),
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

/*@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light"
)
@Composable
fun ReviewSwipeControlsPreview() {
    MoreStuffTheme {
        ReviewSwipeControls({ null }, { null }, {})
    }
}*/

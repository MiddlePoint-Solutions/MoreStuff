package co.softov.morestuff.android.ui.review

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.findViewTreeLifecycleOwner
import co.softov.morestuff.android.BuildConfig
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.presentation.presenter.ReviewModel
import co.softov.morestuff.android.presentation.presenter.ReviewRound
import co.softov.morestuff.android.ui.compose.ProvideLocalViewModelStoreOwner
import co.softov.morestuff.android.ui.compose.SlideAnimation
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import co.softov.morestuff.android.ui.model.ReviewItemUiModel
import co.softov.morestuff.android.ui.review.swipeable.*
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.reviewIconTint
import co.softov.morestuff.android.ui.theme.surfaceContainer
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun ReviewScreen(
    modifier: Modifier = Modifier,
) {
    val navigation = LocalAppNavigation.current
    val lifecycleOwner = LocalView.current.findViewTreeLifecycleOwner()
    val scope = rememberCoroutineScope()
    ProvideLocalViewModelStoreOwner(lifecycleOwner) {
        ReviewContent(
            onBack = navigation::pop,
            modifier = modifier,
            showTaskChat = { taskId ->
                scope.launch {
                    navigation.push(Screen.TaskChat(taskId))
                }
            }
        )
    }
}

@Composable
fun ReviewContent(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReviewViewModel = koinViewModel(),
    showTaskChat: (taskId: Long) -> Unit,
) {
    val scope = rememberCoroutineScope()

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

                    PriorityReviewTopBar(navigateUp = onBack)

                    val states =
                        model.items.map { it to rememberSwipeableCardState(model.round) }

                    val visibleState = remember(model.round) {
                        MutableTransitionState(false)
                    }

                    SlideAnimation(
                        visibleState = visibleState,
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
                        onSwiped = viewModel::onTaskSwiped,
                        onComplete = {
                            scope.launch {
                                states.firstVisibleStateOrNull()?.onComplete()
                                viewModel.completeTask(it)
                            }
                        },
                        showTaskChat = showTaskChat
                    )

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
) {
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
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
private fun ReviewSwipeControls(
    lastItemSwiped: () -> Pair<ReviewItemUiModel, SwipeableCardState>?,
    firstVisibleState: () -> SwipeableCardState?,
    undoAction: (ReviewItemUiModel) -> Unit,
    modifier: Modifier = Modifier
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
    showTaskChat: (taskId: Long) -> Unit,
) {
    val itemClick by rememberUpdatedState(showTaskChat)
    Box(
        modifier = modifier.padding(20.dp)
    ) {
        states.forEach { (task, state) ->
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

@Composable
private fun RoundInfo(
    model: ReviewModel,
    modifier: Modifier = Modifier,
) {

    val info by remember(model.round) {
        mutableStateOf("Round ${model.round}")
    }
    val instructions by remember(model.round) {
        when (model.round) {
            ReviewRound.Review -> "<-- Low    High -->"
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
                mutableStateOf("Total: ${model.items.size}")
            }

            Text(
                text = total,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )

            val debug = with(model) {
                "RoundItems: ${items.size}"
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

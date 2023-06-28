package co.softov.morestuff.android.ui.home

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import co.softov.morestuff.android.app.util.LifecycleEventsObserver
import co.softov.morestuff.android.ui.chat.Messages
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.components.MoreStuffHomeScaffold
import co.softov.morestuff.android.ui.compose.SlideAnimation
import co.softov.morestuff.android.ui.input.UserInput
import co.softov.morestuff.android.ui.list.ListsContent
import co.softov.morestuff.android.ui.priority.PriorityInput
import co.softov.morestuff.android.ui.schedule.PriorityContent
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    showTaskChat: (taskId: Long) -> Unit,
    showSettings: () -> Unit,
    showReview: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    MoreStuffHomeScaffold(
        snackbarHostState = snackbarHostState,
        showSettings = showSettings,
        showReview = showReview,
        content = {
            Box(Modifier.padding(top = it.calculateTopPadding())) {
                HomeContent(
                    showTaskChat = showTaskChat,
                    snackbarHostState = snackbarHostState
                )
            }
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    showTaskChat: (taskId: Long) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val priorityScrollState = rememberLazyListState()
    val chatScrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val viewModel: HomeViewModel = koinViewModel()

    LifecycleEventsObserver(
        onResume = { viewModel.onResume() }
    )

    val messages by viewModel.messages.collectAsState()
    var showTaskLists by remember { mutableStateOf(false) }

    val chatActions = remember {
        ChatActions(
            scheduleAction = viewModel::scheduleResponse,
            taskChatAction = showTaskChat,
        )
    }

    val visibleState = remember {
        MutableTransitionState(true)
    }

    if (showTaskLists) {
        ModalBottomSheet(
            onDismissRequest = { showTaskLists = false },
            content = { ListsContent(itemAction = showTaskChat) }
        )
    }

    Surface {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {

            PriorityContent(
                snackBarHostState = snackbarHostState,
                showTaskChat = showTaskChat,
                modifier = Modifier.fillMaxSize(),
                listState = priorityScrollState,
                onItemDragging = { visibleState.targetState = !it }
            )

            SlideAnimation(
                visibleState = visibleState,
            ) {
                Box {

                    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
                        bottomSheetState = rememberStandardBottomSheetState(
                            skipHiddenState = true
                        )
                    )

                    ConstraintLayout {
                        val (bottomSheet, userInput) = createRefs()

                        Box(
                            modifier = Modifier
                                .constrainAs(bottomSheet) { bottom.linkTo(userInput.top) }
                                .fillMaxSize(),
                        ) {
                            BottomSheetScaffold(
                                scaffoldState = bottomSheetScaffoldState,
                                sheetContent = {

                                    Messages(
                                        messages = messages,
                                        actions = chatActions,
                                        modifier = modifier,
                                        scrollState = chatScrollState
                                    )

                                },
                                sheetPeekHeight = 30.dp
                            ) {}
                        }

                        Column(
                            modifier = Modifier
                                .constrainAs(userInput) { bottom.linkTo(parent.bottom) }
                                .imePadding()
                        ) {

                            PriorityInput(
                                priority = viewModel.priorityModel,
                                planModel = viewModel.planModel,
                                onPriorityChange = viewModel::priorityChanged,
                                onTimeChange = viewModel::updatePlanTime,
                                onDateChange = viewModel::updatePlanDate,
                            )

                            UserInput(
                                showTaskListAction = { showTaskLists = true },
                                onMessageSent = viewModel::addNewTask,
                                resetScroll = {
                                    scope.launch {
                                        delay(200)
                                        priorityScrollState.animateScrollToItem(index = 0)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MainContentPreview() {
    MoreStuffTheme {
        HomeScreen(
            showTaskChat = {},
            showSettings = {},
            showReview = {}
        )
    }
}


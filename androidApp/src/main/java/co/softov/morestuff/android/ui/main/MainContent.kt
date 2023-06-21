package co.softov.morestuff.android.ui.main

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
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
import co.softov.morestuff.android.ui.chat.TaskActions
import co.softov.morestuff.android.ui.compose.SlideAnimation
import co.softov.morestuff.android.ui.input.UserInput
import co.softov.morestuff.android.ui.priority.PriorityInput
import co.softov.morestuff.android.ui.schedule.PriorityContent
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier,
) {
    val priorityScrollState = rememberLazyListState()
    val chatScrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val viewModel: MainViewModel = koinViewModel()

    LifecycleEventsObserver(
        onResume = { viewModel.onResume() }
    )

    val messages by viewModel.messages.collectAsState()
    var showChat by remember { mutableStateOf(false) }

    val chatActions = remember {
        TaskActions(
            scheduleAction = viewModel::scheduleResponse,
            taskChatAction = viewModel::showTaskChat,
        )
    }

    val visibleState = remember {
        MutableTransitionState(true)
    }

    Surface {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {

            PriorityContent(
                snackBarHostState = snackbarHostState,
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

                        // Creating a Bottom Sheet
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
                                showTaskListAction = viewModel::showTaskList,
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
        MainContent()
    }
}


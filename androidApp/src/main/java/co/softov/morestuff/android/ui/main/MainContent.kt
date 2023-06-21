package co.softov.morestuff.android.ui.main

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import co.softov.morestuff.android.app.util.LifecycleEventsObserver
import co.softov.morestuff.android.ui.chat.Messages
import co.softov.morestuff.android.ui.chat.TaskActions
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
                listState = priorityScrollState
            )

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


            //                    MoreStuffSystemChat(
            //                        onClick = {
            //                            visibleState.targetState = !visibleState.targetState
            //                        }
            //                    )
            //


        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MoreStuffSystemChat(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 18.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Image(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(56.dp)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically),
                painter = rememberVectorPainter(image = Icons.Default.Flare),
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(56.dp)
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Mr. Stuff",
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(4.dp)
                )
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


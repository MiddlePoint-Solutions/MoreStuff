package co.softov.morestuff.android.ui.home

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.ui.chat.Messages
import co.softov.morestuff.android.ui.chat.items.MockData.chatActions
import co.softov.morestuff.android.ui.components.MoreStuffHomeScaffold
import co.softov.morestuff.android.ui.input.UserInput
import co.softov.morestuff.android.ui.input.UserInputViewModel
import co.softov.morestuff.android.ui.input.UserTextInput
import co.softov.morestuff.android.ui.input.VoiceToTextInput
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import co.softov.morestuff.android.ui.priority.PriorityInput
import co.softov.morestuff.android.ui.schedule.PriorityContent
import co.softov.morestuff.android.ui.schedule.PriorityViewModel
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.arkivanov.decompose.router.stack.push
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    val navigation = LocalAppNavigation.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    MoreStuffHomeScaffold(
        snackbarHostState = snackbarHostState,
        topAppBarScrollBehavior = scrollBehavior,
        content = {
            HomeContent(
                showTaskChat = { taskId ->
                    scope.launch {
                        navigation.push(Screen.TaskChat(taskId))
                    }
                },
                snackbarHostState = snackbarHostState,
                modifier = Modifier.padding(top = it.calculateTopPadding()),
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    showTaskChat: (taskId: Long) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    userInputViewModel: UserInputViewModel = koinViewModel(),
    priorityViewModel: PriorityViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val priorityModel by userInputViewModel.priorityModel.collectAsStateWithLifecycle()
    var isBottomSheetVisible by remember { mutableStateOf(false) }
    val messages by homeViewModel.messages.collectAsStateWithLifecycle()
    val scrollState = rememberLazyListState()
    val priorityScrollState = rememberLazyListState()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            PriorityContent(
                snackBarHostState = snackbarHostState,
                showTaskChat = showTaskChat,
                onCallToAction = { focusRequester.requestFocus() },
                listState = priorityScrollState,
                modifier = Modifier.weight(0.8f).padding(bottom = 30.dp),
            )
        }

        FloatingActionButton(
            onClick = {
                isBottomSheetVisible = true
                priorityViewModel.showContent()
            },
            modifier = Modifier
                .padding(16.dp, bottom = 48.dp, end = 20.dp)
                .size(50.dp)
                .align(Alignment.BottomEnd),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }

    if (isBottomSheetVisible) {
        BackHandler(onBack = {
            scope.launch {
                bottomSheetState.hide()
            }
        })
        ModalBottomSheet(
            onDismissRequest = {
                isBottomSheetVisible = false
            },
            sheetState = bottomSheetState,
            content = {
                BoxWithConstraints {
                    LaunchedEffect(isBottomSheetVisible) {
                        delay(200)
                        focusRequester.requestFocus()
                    }
                    Column(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Messages(
                            messages = messages,
                            actions = chatActions,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(4f),
                            scrollState = scrollState,
                        )

                        PriorityInput(
                            model = priorityModel,
                            onNowSelected = userInputViewModel::setNowPriority,
                            onLaterSelected = userInputViewModel::setLaterPriority,
                            onPlanSelected = userInputViewModel::setPlanPriority,
                            onTimeChange = userInputViewModel::updatePlanTime,
                            onDateChange = userInputViewModel::updatePlanDate,

                            )

                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .padding(bottom = 6.dp)
                        ) {
                            var userInputValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                                mutableStateOf(TextFieldValue(text = userInputViewModel.userInput))
                            }

                            LaunchedEffect(userInputViewModel.userInput) {
                                userInputValue =
                                    userInputValue.copy(text = userInputViewModel.userInput)
                            }

                            UserInput(
                                textContent = {
                                    UserTextInput(
                                        value = userInputValue,
                                        onValueChange = { userInputValue = it },
                                        focusRequester = focusRequester,
                                        sendAction = { title ->
                                            scope.launch {
                                                userInputViewModel.createNewTask(title)
                                                userInputValue = userInputValue.copy("")
                                                delay(100)
                                                when (priorityModel.priority) {
                                                    PriorityModel.Later -> priorityScrollState.scrollToItem(
                                                        index = priorityViewModel.tasks.size - 1
                                                    )

                                                    PriorityModel.Now -> priorityScrollState.animateScrollToItem(
                                                        index = 0
                                                    )

                                                    is PriorityModel.Plan -> {}
                                                }
                                            }
                                        },
                                        actionsContent = {
                                            VoiceToTextInput(
                                                onUpdateValue = userInputViewModel::updateUserInput
                                            )
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        )
    }
}


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light"
)
@Composable
fun MainContentPreview() {
    MoreStuffTheme {
        HomeScreen()
    }
}


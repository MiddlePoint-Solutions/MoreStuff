package co.softov.morestuff.android.ui.home

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.ui.chat.Messages
import co.softov.morestuff.android.ui.chat.items.MockData.chatActions
import co.softov.morestuff.android.ui.components.MoreStuffHomeScaffold
import co.softov.morestuff.android.ui.components.MoreStuffTopBar
import co.softov.morestuff.android.ui.input.UserInput
import co.softov.morestuff.android.ui.input.UserInputViewModel
import co.softov.morestuff.android.ui.input.UserTextInput
import co.softov.morestuff.android.ui.input.VoiceToTextInput
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import co.softov.morestuff.android.ui.priority.PriorityInput
import co.softov.morestuff.android.ui.schedule.NotificationState
import co.softov.morestuff.android.ui.schedule.PriorityContent
import co.softov.morestuff.android.ui.schedule.PriorityViewModel
import co.softov.morestuff.android.ui.schedule.TaskOptionsDialog
import co.softov.morestuff.android.ui.search.SearchBar
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.utils.explode
import com.arkivanov.decompose.router.stack.push
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.PartySystem
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    val navigation = LocalAppNavigation.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var itemSelected by remember { mutableStateOf(false) }
    var isSearching by rememberSaveable { mutableStateOf(false) }

    MoreStuffHomeScaffold(
        snackbarHostState = snackbarHostState,
        topAppBarScrollBehavior = scrollBehavior,
        topBar = {
            MoreStuffTopBar(
                reviewSelected = { navigation.push(Screen.Review) },
                settingsSelected = { navigation.push(Screen.Settings) },
                searchSelected = { isSearching = true },
                scrollBehavior = scrollBehavior,
                itemSelectedState = itemSelected,
                closeBulkMode = { itemSelected = false }
            )
        },
        content = {
            HomeContent(
                showTaskChat = { taskId ->
                    scope.launch {
                        navigation.push(Screen.TaskChat(taskId))
                    }
                },
                snackbarHostState = snackbarHostState,
                itemSelectedState = { isSelected ->
                    itemSelected = isSelected
                },
                modifier = Modifier.padding(top = it.calculateTopPadding()),
            )
        },
    )

    AnimatedVisibility(
        visible = isSearching,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        SearchBar(
            onSearchClose = { isSearching = false },
            showTaskChat = { navigation.push(Screen.TaskChat(it)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
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
    itemSelectedState: (Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val priorityModel by userInputViewModel.priorityModel.collectAsStateWithLifecycle()
    var showTaskInput by remember { mutableStateOf(false) }
    val messages by homeViewModel.messages.collectAsStateWithLifecycle()
    val scrollState = rememberLazyListState()
    val priorityScrollState = rememberLazyListState()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var taskOptions by remember { mutableLongStateOf(0) }
    var showTaskCompleteAnimation by remember { mutableLongStateOf(0) }

    val tasks by priorityViewModel.tasks.collectAsStateWithLifecycle()
    val model by priorityViewModel.model.collectAsStateWithLifecycle()
    val selectedTaskIds by priorityViewModel.selectedTaskIds.collectAsState()

    val showEmptyState by remember(tasks) { derivedStateOf { tasks.isEmpty() } }

    LaunchedEffect(model.taskSelectionEnabled) {
        itemSelectedState(model.taskSelectionEnabled)
    }

    BackHandler(model.taskSelectionEnabled) {
        priorityViewModel.clearSelectedTasks()
    }

    val resources = LocalContext.current.resources
    LaunchedEffect(priorityViewModel.notification) {
        when (priorityViewModel.notification) {
            NotificationState.Complete -> {
                snackbarHostState.showSnackbar(
                    message = resources.getString(R.string.snack_task_completed),
                    actionLabel = resources.getString(R.string.undo),
                    duration = SnackbarDuration.Long
                ).also {
                    when (it) {
                        SnackbarResult.Dismissed -> priorityViewModel.resetNotification()
                        SnackbarResult.ActionPerformed -> priorityViewModel.undoLastCompleted()
                    }
                }
            }

            else -> {}
        }
    }

    if (taskOptions > 0) {
        val sheetState = rememberModalBottomSheetState()
        val dismissDialog = { taskOptions = 0 }
        TaskOptionsDialog(
            sheetState = sheetState,
            dismissDialog = dismissDialog,
            completeTask = {
                showTaskCompleteAnimation = taskOptions
                scope.launch {
                    priorityViewModel.completeTask(taskOptions)
                    sheetState.hide()
                    dismissDialog()
                }
            },
            moveToTop = {
                scope.launch {
                    priorityViewModel.moveToTop(taskOptions)
                    sheetState.hide()
                    dismissDialog()
                }
            },
            moveToBottom = {
                scope.launch {
                    priorityViewModel.moveToBottom(taskOptions)
                    sheetState.hide()
                    dismissDialog()
                }
            }
        )
    }

    if (priorityViewModel.showDeleteConfirmDialog) {
        ConfirmDeleteDialog(
            onConfirm = { priorityViewModel.deleteSelectedTasks() },
            onDismiss = {
                priorityViewModel.dismissDeleteDialog()
            }
        )
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        PriorityContent(
            tasks = tasks,
            selectedTaskIds = selectedTaskIds,
            onItemClick = { taskId ->
                if (model.taskSelectionEnabled) {
                    priorityViewModel.toggleTaskSelection(taskId)
                } else {
                    showTaskChat(taskId)
                }
            },
            onItemLongClick = priorityViewModel::toggleTaskSelection,
            showTaskOptions = { taskOptions = it },
            toggleQuickReminder = priorityViewModel::toggleQuickReminder,
            listState = priorityScrollState,
            modifier = Modifier.padding(bottom = 30.dp),
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

        FloatingActionButton(
            onClick = {
                showTaskInput = true
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

    if (showEmptyState) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 180.dp),
            contentAlignment = Alignment.Center
        ) {
            TextButton(onClick = { showTaskInput = true }) {
                Text(
                    stringResource(R.string.empty_priority_list_cta),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }

    if (showTaskInput) {
        BackHandler(onBack = {
            scope.launch {
                bottomSheetState.hide()
            }
        })
        ModalBottomSheet(
            onDismissRequest = {
                showTaskInput = false
            },
            sheetState = bottomSheetState,
            content = {
                BoxWithConstraints {
                    LaunchedEffect(showTaskInput) {
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
                                                        index = tasks.size - 1
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

@Composable
private fun ConfirmDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.confirm_delete)) },
        text = {
            Text(
                text = stringResource(R.string.sure_delete_task),
                textAlign = TextAlign.Start

            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
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


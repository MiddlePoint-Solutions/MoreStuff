package co.softov.morestuff.android.ui.chat.task

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.model.Task
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.chat.Messages
import co.softov.morestuff.android.ui.chat.task.model.TaskPriorityModel
import co.softov.morestuff.android.ui.priority.PriorityButton
import co.softov.morestuff.android.ui.priority.TaskPriorityBottomSheet
import co.softov.morestuff.android.ui.priority.TaskPriorityViewModel
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.google.accompanist.insets.ui.Scaffold
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TaskChatContent(
    taskId: Long,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val viewModel = getViewModel<TaskChatViewModel>(key = "TaskChatVM") {
        parametersOf(taskId)
    }

    val priorityViewModel = getViewModel<TaskPriorityViewModel> {
        parametersOf(taskId)
    }

    val chatActions = ChatActions(
        scheduleAction = viewModel::scheduleResponse,
    )

    val messages by viewModel.messages.collectAsState()
    val taskPriorityModel by priorityViewModel.model.collectAsState()

    var openBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberSheetState()

    val bottomSheetDismissAction: () -> Unit = {
        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
            openBottomSheet = false
        }
    }
    var messageText by remember { mutableStateOf("") }

    BackHandler(bottomSheetState.isVisible) {
        scope.launch {
            bottomSheetState.hide()
        }.invokeOnCompletion {
            openBottomSheet = false
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TaskChatTopBar(
                taskId = taskId,
                editScheduleAction = {
                    priorityViewModel.reset()
                    openBottomSheet = true
                },
            )
        },
        content = {
            Surface(modifier.padding(it)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .imePadding(),
                ) {
                    Messages(
                        messages = messages,
                        actions = chatActions,
                        modifier = modifier.weight(1f),
                        scrollState = scrollState
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .imePadding()
                    ) {
                        TextField(
                            value = messageText,
                            onValueChange = { newText -> messageText = newText },
                            label = { Text("Write a message") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                               ,
                            keyboardActions = KeyboardActions(onDone = {
                                if (messageText.isNotBlank()) {
                                    viewModel.sendMessage(content = messageText.trim())
                                    messageText = ""
                                }
                            }),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                        )
                        IconButton(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    viewModel.sendMessage(content = messageText.trim())
                                    messageText = ""
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(Icons.Filled.Send, contentDescription = "send mensaje")
                        }
                    }
                }
            }
        }
    )

    TaskPriorityBottomSheet(
        model = taskPriorityModel,
        openBottomSheet = openBottomSheet,
        bottomSheetState = bottomSheetState,
        priorityChangeAction = priorityViewModel::priorityChanged,
        priorityOptionChangeAction = priorityViewModel::onPriorityOptionChanged,
        confirmationAction = {
            priorityViewModel.updateTaskSchedule()
            bottomSheetDismissAction()
        },
        dismissAction = bottomSheetDismissAction
    )
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TaskChatTopBar(
    taskId: Long,
    editScheduleAction: () -> Unit,
) {

    // TODO: hoist state out of toolbar
    val viewModel = getViewModel<TaskChatViewModel>(key = "TaskChatVM") {
        parametersOf(taskId)
    }
    val priorityViewModel = getViewModel<TaskPriorityViewModel> {
        parametersOf(taskId)
    }
    val priority by priorityViewModel.model.collectAsState()
    val task by viewModel.task.collectAsState()
    val schedule by viewModel.schedule.collectAsState()

    val focusManager = LocalFocusManager.current
    Surface(
        color = Color(0xff2B3438),
        tonalElevation = 10.dp,
    ) {
        Column {
            TopAppBar(
                title = { },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xff2B3438)
                ),
                actions = {
                    when (task.isComplete) {
                        true -> {
                            IconButton(onClick = { viewModel.setTaskComplete(false) }) {
                                Icon(
                                    imageVector = Icons.Filled.Replay,
                                    contentDescription = stringResource(R.string.cd_task_complete)
                                )
                            }
                        }
                        false -> {
                            IconButton(onClick = { viewModel.setTaskComplete(true) }) {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = stringResource(R.string.cd_task_complete)
                                )
                            }
                        }
                    }

                },
                navigationIcon = {
                    IconButton(onClick = viewModel::onBackPressed) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_back)
                        )
                    }
                }
            )

            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurface
            ) {
                Column(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .windowInsetsPadding(
                            WindowInsets.safeContent.union(WindowInsets.ime)
                        )
                ) {
                    BasicTextField(
                        value = viewModel.taskTitle,
                        onValueChange = viewModel::updateTaskTitle,
                        modifier = Modifier
                            .fillMaxWidth(),
                        readOnly = task.isComplete,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            autoCorrect = false,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions {
                            focusManager.clearFocus()
                        },
                        maxLines = 4,
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            textDecoration = when (task.isComplete) {
                                true -> TextDecoration.LineThrough
                                false -> null
                            }
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = stringResource(R.string.cd_schedule_icon)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        ScheduleButton(schedule, priority, editScheduleAction, task)
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleButton(
    schedule: Schedule?,
    priority: TaskPriorityModel,
    editScheduleAction: () -> Unit,
    task: Task,
    timeFormatter: TimeFormatter = get(),
) {
    val title = when {
        schedule == null -> {
            stringResource(R.string.task_chat_schedule_reminder)
        }
        schedule.scheduleUtcTime == null -> {
            stringResource(R.string.time_option_later)
        }
        else -> {
            val formattedTime = timeFormatter.formatTimeOnly(schedule.scheduleUtcTime) ?: ""

            when (priority.priorityModel.priority) {
                is Priority.Today -> stringResource(id = R.string.time_option_today, formattedTime)
                is Priority.Tomorrow -> stringResource(
                    id = R.string.time_option_tomorrow,
                    formattedTime
                )
                is Priority.Later -> timeFormatter.formatTimeDayAndMonth(schedule.scheduleUtcTime)
            }
        }
    }.orEmpty()

    PriorityButton(
        onSelected = editScheduleAction,
        text = title,
        shape = RoundedCornerShape(percent = 50),
        enabled = !task.isComplete
    )
}

@Preview
@Composable
private fun TaskChatTopBarPreview() {
    MoreStuffTheme(darkTheme = true) {
        TaskChatTopBar(
            taskId = 1,
            editScheduleAction = { },
        )
    }
}

@Preview
@Composable
private fun TaskChatPreview() {
    MoreStuffTheme(darkTheme = true) {
        TaskChatContent(
            taskId = 1
        )
    }
}

package co.softov.morestuff.android.ui.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import co.softov.morestuff.android.ui.priority.TaskPriorityBottomSheet
import co.softov.morestuff.android.ui.priority.PriorityButton
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.google.accompanist.insets.ui.Scaffold
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
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

    val chatActions = ChatActions(
        scheduleAction = viewModel::scheduleResponse,
    )

    val messages by viewModel.messages.collectAsState()

    var openBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberSheetState()

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
                editScheduleAction = { openBottomSheet = true },
            )
        },
        content = {
            Surface(modifier.padding(it)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Messages(
                        messages = messages,
                        actions = chatActions,
                        modifier = modifier.weight(1f),
                        scrollState = scrollState
                    )
                }
            }
        }
    )

    TaskPriorityBottomSheet(
        taskId,
        openBottomSheet,
        bottomSheetState,
        dismissAction = {
            openBottomSheet = false
        })
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TaskChatTopBar(
    taskId: Long,
    editScheduleAction: () -> Unit,
) {

    val viewModel = getViewModel<TaskChatViewModel>(key = "TaskChatVM") {
        parametersOf(taskId)
    }

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

                        PriorityButton(
                            onSelected = editScheduleAction,
                            text = schedule?.scheduleTimeLocal
                                ?: stringResource(R.string.task_chat_schedule_reminder),
                            shape = RoundedCornerShape(percent = 50),
                            enabled = !task.isComplete
                        )
                    }
                }
            }
        }
    }
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

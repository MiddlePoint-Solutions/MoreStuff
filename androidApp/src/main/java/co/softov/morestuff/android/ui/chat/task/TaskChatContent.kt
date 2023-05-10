package co.softov.morestuff.android.ui.chat.task

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import co.softov.morestuff.android.ui.chat.TaskActions
import co.softov.morestuff.android.ui.chat.Messages
import co.softov.morestuff.android.ui.chat.task.model.TaskPriorityModel
import co.softov.morestuff.android.ui.priority.PriorityButton
import co.softov.morestuff.android.ui.priority.TaskPriorityBottomSheet
import co.softov.morestuff.android.ui.priority.TaskPriorityViewModel
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.google.accompanist.insets.ui.Scaffold
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
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

    val priorityViewModel = getViewModel<TaskPriorityViewModel> {
        parametersOf(taskId)
    }

    val taskActions = TaskActions(
        scheduleAction = viewModel::scheduleResponse,
    )

    val messages by viewModel.messages.collectAsState()

    var openBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()

    val bottomSheetDismissAction: () -> Unit = {
        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
            openBottomSheet = false
        }
    }


    BackHandler(bottomSheetState.isVisible) {
        scope.launch {
            bottomSheetState.hide()
        }.invokeOnCompletion {
            openBottomSheet = false
        }
    }

    var isExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBarTaskChat(
                viewModel,
                setTaskComplete = { complete -> viewModel.setTaskComplete(complete) },
                onBackPressed = viewModel::onBackPressed,
                isExpanded = isExpanded
            )
        },
        modifier = modifier
    ) {

        Box(Modifier.fillMaxSize()) {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .imePadding(),
                ) {
                    TaskChatTopBarEditTask(
                        taskId = taskId,
                        editScheduleAction = {
                            priorityViewModel.reset()
                            openBottomSheet = true
                        },
                        isExpanded = isExpanded,
                        setIsExpanded = { value -> isExpanded = value }
                    )


                    Messages(
                        messages = messages,
                        actions = taskActions,
                        modifier = modifier.weight(1f),
                        scrollState = scrollState
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .imePadding()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = MaterialTheme.colorScheme.primary)
                        ) {
                            TaskMessageTextField(
                                sendMessageForTask = { content ->
                                    viewModel.sendMessageForTask(
                                        content
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}






@Composable
fun TaskChatTopBarEditTask(
    taskId: Long,
    editScheduleAction: () -> Unit,
    isExpanded: Boolean,
    setIsExpanded: (Boolean) -> Unit,
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
    val onBackPressed = {
        if (isExpanded) {
            focusManager.clearFocus()
            setIsExpanded(false)
        }
    }
    BackHandler(isExpanded, onBackPressed)

    Surface(
        modifier = Modifier
            .animateContentSize(animationSpec = snap())
            .then(
                if (isExpanded) {
                    Modifier.fillMaxHeight()
                } else {
                    Modifier.height(IntrinsicSize.Min)
                }
            ),
        color = Color(0xff2B3438),
        tonalElevation = 10.dp,
    ) {
        Box {
            Column(Modifier.align(Alignment.TopStart)) {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onSurface
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = 45.dp)
                            .windowInsetsPadding(
                                WindowInsets.safeContent.union(WindowInsets.ime)
                            )
                            .weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 15.dp)
                                .onFocusChanged { focusState ->
                                    setIsExpanded(focusState.isFocused)
                                },
                        ) {
                            BasicTextField(
                                value = viewModel.taskTitle,
                                onValueChange = viewModel::updateTaskTitle,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .fillMaxWidth()
                                    .onFocusChanged { focusState ->
                                        if (!task.isComplete) {
                                            setIsExpanded(focusState.isFocused)
                                        } else {
                                            focusManager.clearFocus()
                                        }
                                    },
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
                            LaunchedEffect(task.isComplete) {
                                if (task.isComplete) {
                                    focusManager.clearFocus()
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = stringResource(R.string.cd_schedule_icon),
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .alpha(if (isExpanded) 0f else 1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Crossfade(targetState = isExpanded) { expanded ->
                            if (!expanded) {
                                ScheduleButton(
                                    schedule,
                                    priority,
                                    editScheduleAction,
                                    task,
                                    modifier = Modifier.alpha(if (isExpanded) 0f else 1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ScheduleButton(
    schedule: ScheduleDomain?,
    priority: TaskPriorityModel,
    editScheduleAction: () -> Unit,
    task: TaskDomain,
    timeFormatter: TimeFormatter = get(),
    modifier: Modifier,
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
                is Priority.Now -> stringResource(
                    id = R.string.time_option_today,
                    formattedTime
                )

                is Priority.Later -> stringResource(
                    id = R.string.time_option_tomorrow,
                    formattedTime
                )

                is Priority.Plan -> timeFormatter.formatTimeDayAndMonth(schedule.scheduleUtcTime)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarTaskChat(
    viewModel: TaskChatViewModel,
    setTaskComplete: (Boolean) -> Unit,
    onBackPressed: () -> Unit,
    isExpanded: Boolean,
) {

    val task by viewModel.task.collectAsState()
    val alphaValue by animateFloatAsState(
        targetValue = if (isExpanded) 0.3f else 1f,
        animationSpec = tween(durationMillis = 500)
    )

    TopAppBar(
        title = { },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xff2B3438)
        ),
        actions = {
            if (!isExpanded) {
                when (task.isComplete) {
                    true -> {
                        IconButton(onClick = { setTaskComplete(false) }) {
                            Icon(
                                imageVector = Icons.Filled.Replay,
                                contentDescription = stringResource(R.string.cd_task_complete),
                                modifier = Modifier.alpha(alphaValue)
                            )
                        }
                    }

                    false -> {
                        IconButton(onClick = { setTaskComplete(true) }) {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = stringResource(R.string.cd_task_complete),
                                modifier = Modifier.alpha(alphaValue)
                            )
                        }
                    }
                }
            }
        },
        navigationIcon = {
            if (!isExpanded) {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_navigate_back)
                    )
                }
            }
        }
    )
}

@Composable
fun TaskMessageTextField(
    sendMessageForTask: (String) -> Unit,
) {
    var messageText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = messageText,
            onValueChange = { newText -> messageText = newText },
            enabled = true,
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .defaultMinSize(minHeight = 46.dp)
                .padding(
                    start = 16.dp,
                    top = 8.dp,
                    bottom = 8.dp,
                    end = 4.dp
                )
                .onFocusChanged {},
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                if (messageText.isNotBlank()) {
                    sendMessageForTask(messageText.trim())
                    messageText = ""
                }
            }),
            maxLines = Int.MAX_VALUE,
            cursorBrush = SolidColor(LocalContentColor.current),
            textStyle = LocalTextStyle.current.copy(
                color = LocalContentColor.current,
                fontSize = 18.sp
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (messageText.isEmpty()) {
                        Text(
                            text = "Write something...",
                            fontSize = 18.sp
                        )
                    }
                    innerTextField()
                }
            }
        )

        IconButton(
            onClick = {
                if (messageText.isNotBlank()) {
                    sendMessageForTask(messageText.trim())
                    messageText = ""
                }
            },
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Icon(Icons.Filled.Send, contentDescription = "send mensaje")
        }
    }
}


@Preview
@Composable
fun TaskChatTopBarPreview() {
    MoreStuffTheme(darkTheme = true) {
        TaskChatTopBarEditTask(
            taskId = 1,
            editScheduleAction = { },
            isExpanded = true,
            setIsExpanded = {}

        )
    }
}

@Preview
@Composable
fun TaskChatPreview() {
    MoreStuffTheme(darkTheme = true) {
        TaskChatContent(
            taskId = 1
        )
    }
}

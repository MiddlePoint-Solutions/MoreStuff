package co.softov.morestuff.android.ui.chat.task

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.*
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.util.LifecycleViewModelStoreOwner
import co.softov.morestuff.android.domain.enums.RelativeDateDisplay
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.chat.Messages
import co.softov.morestuff.android.ui.home.PlanModel
import co.softov.morestuff.android.ui.priority.PriorityButton
import co.softov.morestuff.android.ui.priority.PriorityDatePicker
import co.softov.morestuff.android.ui.priority.PriorityTimePicker
import co.softov.morestuff.android.ui.priority.SchedulePermissionRequester
import co.softov.morestuff.android.ui.priority.getRelativeDate
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.google.accompanist.insets.ui.Scaffold
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun ProvideLocalViewModelStoreOwner(
    localViewModelStoreOwner: ViewModelStoreOwner,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalViewModelStoreOwner provides localViewModelStoreOwner,
        content = content
    )
}

@Composable
fun TaskChatScreen(
    taskId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalView.current.findViewTreeLifecycleOwner()
    ProvideLocalViewModelStoreOwner(LifecycleViewModelStoreOwner(lifecycleOwner)) {
        TaskChatContent(
            taskId = taskId,
            onBack = onBack,
            viewModel = koinViewModel { parametersOf(taskId) },
            modifier = modifier
        )
    }
}

@Composable
private fun TaskChatContent(
    taskId: Long,
    onBack: () -> Unit,
    viewModel: TaskChatViewModel,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyListState()

    val shareImage = rememberUpdatedState<(String) -> Unit> { imagePath ->
        viewModel.shareImage(imagePath)
    }

    val messages by viewModel.messages.collectAsState()

    var selectImageFromGallery by remember { mutableStateOf<Uri?>(null) }
    var selectedImageMessage by remember { mutableStateOf<Message?>(null) }

    val chatActions = ChatActions(
        scheduleAction = viewModel::scheduleResponse,
        copyMessage = { message ->
            viewModel.copyToClipboard(message.content)
        },
        deleteMessage = { message ->
            viewModel.deleteMessage(messageId = message.id)
        },
        onImageSelected = {
            selectedImageMessage = it
        }
    )

    val pickImage = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        selectImageFromGallery = uri
    }

    var isExpanded by remember { mutableStateOf(false) }

    when {
        selectedImageMessage != null -> selectedImageMessage?.let {
            BackHandler {
                selectedImageMessage = null
            }
            ImagePreviewScreen(
                imagePath = it.messageData?.filePath ?: "",
                cancel = {
                    selectedImageMessage = null
                },
                onSendImage = { imagePath ->
                    shareImage.value.invoke(imagePath)
                },
                message = it,
            )
        }

        selectImageFromGallery != null -> selectImageFromGallery?.let {
            BackHandler {
                selectImageFromGallery = null
            }
            ImageImportScreen(
                imageUri = it,
                send = { message ->
                    viewModel.sendImageMessageForTask(it.toString(), message)
                    selectImageFromGallery = null
                },
                cancel = {
                    selectImageFromGallery = null
                }
            )
        }

        else -> {
            Scaffold(
                topBar = {
                    TopAppBarTaskChat(
                        viewModel,
                        setTaskComplete = { complete -> viewModel.setTaskComplete(complete) },
                        onBackPressed = onBack,
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
                                isExpanded = isExpanded,
                                setIsExpanded = { value -> isExpanded = value },
                            )


                            Messages(
                                messages = messages,
                                actions = chatActions,
                                modifier = modifier.weight(1f),
                                scrollState = scrollState,
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
                                        sendMessageForTask = viewModel::sendMessageForTask,
                                        pickImages = {
                                            pickImage.launch(
                                                PickVisualMediaRequest(
                                                    PickVisualMedia.ImageOnly
                                                )
                                            )
                                        }
                                    )
                                }
                            }
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
    isExpanded: Boolean,
    setIsExpanded: (Boolean) -> Unit,
) {

    // TODO: hoist state out of toolbar
    val viewModel = getViewModel<TaskChatViewModel>(key = "TaskChatVM") {
        parametersOf(taskId)
    }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    val planModel = viewModel.planModel
    val displayTime by remember(planModel) { mutableStateOf(planModel.planTime) }


    val task by viewModel.task.collectAsState()
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
        color = MaterialTheme.colorScheme.primaryContainer,
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

                        Spacer(modifier = Modifier.width(20.dp))

                        Crossfade(targetState = isExpanded, label = "") { expanded ->
                            if (!expanded) {
                                ScheduleButton(
                                    modifier = Modifier.alpha(if (isExpanded) 0f else 1f),
                                    planModel = viewModel.planModel,
                                    onTimeChange = viewModel::updatePlanTime,
                                    onDateChange = viewModel::updatePlanDate,
                                    onCreatePlanAndReschedule = viewModel::createOneTimeSchedule,
                                    cancelActiveSchedule = viewModel::cancelActiveSchedule,
                                    taskId = taskId,
                                    showDatePickerDialog = showDatePickerDialog,
                                    showTimePickerDialog = showTimePickerDialog,
                                    onShowDatePickerDialogChange = { showDatePickerDialog = it },
                                    onShowTimePickerDialogChange = { showTimePickerDialog = it },
                                    displayTime = displayTime.toString()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleButton(
    planModel: PlanModel,
    onTimeChange: (Int, Int) -> Unit,
    onDateChange: (Long) -> Unit,
    timeFormatter: TimeFormatter = koinInject(),
    modifier: Modifier = Modifier,
    onCreatePlanAndReschedule: () -> Unit,
    cancelActiveSchedule: () -> Unit,
    taskId: Long,
    showDatePickerDialog: Boolean,
    showTimePickerDialog: Boolean,
    onShowDatePickerDialogChange: (Boolean) -> Unit,
    onShowTimePickerDialogChange: (Boolean) -> Unit,
    displayTime: String,
) {
    val viewModel = getViewModel<TaskChatViewModel>(key = "TaskChatVM") {
        parametersOf(taskId)
    }
    val schedule by viewModel.schedule.collectAsState()

    if (showDatePickerDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = planModel.epochMs
        )

        PriorityDatePicker(
            dismissDialog = { onShowDatePickerDialogChange(false) },
            onDateChange = {
                datePickerState.selectedDateMillis?.let {
                    onDateChange(it)
                }
                onShowDatePickerDialogChange(false)
                onShowTimePickerDialogChange(true)

            },
            state = datePickerState,
        )
    }

    if (showTimePickerDialog) {
        val timePickerState = rememberTimePickerState(
            initialHour = planModel.hour,
            initialMinute = planModel.minute
        )
        PriorityTimePicker(
            dismissTimePicker = { onShowTimePickerDialogChange(false) },
            onTimeChange = {
                onTimeChange(
                    timePickerState.hour,
                    timePickerState.minute
                )
                onCreatePlanAndReschedule()
                onShowTimePickerDialogChange(false)
            },
            state = timePickerState
        )
    }

    Row(modifier = modifier) {

        val time by remember(displayTime) {
            derivedStateOf {
                timeFormatter.formatTimeOnly(displayTime)
            }
        }

        val buttonText = when {
            schedule == null -> stringResource(id = R.string.task_chat_schedule_reminder)
            planModel.relativeDisplay == RelativeDateDisplay.Today -> stringResource(id = R.string.relative_today) + " " + (time
                ?: "")

            else -> getRelativeDate(planModel, timeFormatter) + " " + (time ?: "")
        }


        PriorityButton(
            onSelected = { onShowDatePickerDialogChange(true) },
            text = buttonText,
            shape = RoundedCornerShape(percent = 50),
        )
        Spacer(modifier = Modifier.width(16.dp))

        if (schedule != null) {
            IconButton(
                onClick = { cancelActiveSchedule() },
                modifier = Modifier
                    .padding(start = 5.dp)
            ) {
                Icon(
                    Icons.Default.Cancel,
                    contentDescription = "Cancel",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        SchedulePermissionRequester()
    }
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
        animationSpec = tween(durationMillis = 500),
        label = "Task chat top bar alpha animation"
    )

    TopAppBar(
        title = { },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
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
    pickImages: () -> Unit,
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
                .weight(8f)
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
            onClick = pickImages,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
        ) {
            Icon(Icons.Filled.PhotoLibrary, contentDescription = "select images")
        }

        IconButton(
            onClick = {
                if (messageText.isNotBlank()) {
                    sendMessageForTask(messageText.trim())
                    messageText = ""
                }
            },
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
                .padding(end = 8.dp, start = 3.dp)
        ) {
            Icon(Icons.Filled.Send, contentDescription = "send mensaje")
        }
    }
}


@Preview
@Composable
fun TaskChatTopBarPreview() {
    MoreStuffTheme() {
        TaskChatTopBarEditTask(
            taskId = 1,
            isExpanded = true,
            setIsExpanded = {}

        )
    }
}

//@Preview
//@Composable
//fun TaskChatPreview() {
//    MoreStuffTheme() {
//        TaskChatContent(
//            taskId = 1,
//            onBack = {}
//        )
//    }
//}

package co.softov.morestuff.android.ui.chat.task

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.util.LifecycleViewModelStoreOwner
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.chat.Messages
import co.softov.morestuff.android.ui.compose.ProvideLocalViewModelStoreOwner
import co.softov.morestuff.android.ui.home.PlanModel
import co.softov.morestuff.android.ui.image.ImageImportScreen
import co.softov.morestuff.android.ui.image.ImagePreviewScreen
import co.softov.morestuff.android.ui.input.UserInput
import co.softov.morestuff.android.ui.input.UserTextInput
import co.softov.morestuff.android.ui.priority.PriorityButton
import co.softov.morestuff.android.ui.priority.PriorityDatePicker
import co.softov.morestuff.android.ui.priority.PriorityTimePicker
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.google.accompanist.insets.ui.Scaffold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

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

    val shareImage by rememberUpdatedState<(String) -> Unit> { imagePath ->
        viewModel.shareImage(imagePath)
    }

    val messages by viewModel.messages.collectAsStateWithLifecycle()

    var selectImageFromGallery by remember { mutableStateOf<Uri?>(null) }
    val imagePicked by rememberUpdatedState<(Uri) -> Unit> { selectImageFromGallery = it }

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
                    shareImage(imagePath)
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
            TaskChat(
                viewModel,
                onBack,
                modifier,
                taskId,
                messages,
                chatActions,
                imagePicked
            )
        }
    }
}

@Composable
private fun TaskChat(
    viewModel: TaskChatViewModel,
    onBack: () -> Unit = {},
    modifier: Modifier,
    taskId: Long,
    messages: List<Message>,
    chatActions: ChatActions,
    imagePicked: (Uri) -> Unit = {}
) {

    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    val task by viewModel.task.collectAsStateWithLifecycle()

    val pickImage = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            imagePicked(uri)
        } else {
            Timber.d("Image picker uri is NULL!")
        }
    }

    var userInputValue by rememberSaveable(
        key = taskId.toString(),
        stateSaver = TextFieldValue.Saver
    ) {
        mutableStateOf(TextFieldValue())
    }

    Scaffold(
        topBar = {
            TaskChatTopAppBar(
                onBackPressed = onBack,
            )
        },
        modifier = modifier.navigationBarsPadding()
    ) { scaffoldPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(top = scaffoldPadding.calculateTopPadding())
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TaskChatEditor(
                    isComplete = task.isComplete,
                    taskTitle = { viewModel.taskTitle },
                    onTitleChange = viewModel::updateTaskTitle,
                    toggleTaskComplete = viewModel::toggleTaskComplete,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = stringResource(R.string.cd_schedule_icon),
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        ScheduleButton(
                            planModel = viewModel.planModel,
                            schedule = task.activeSchedule,
                            onTimeChange = viewModel::updatePlanTime,
                            onDateChange = viewModel::updatePlanDate,
                            createPlanSchedule = viewModel::createOneTimeSchedule,
                            cancelActiveSchedule = viewModel::cancelActiveSchedule,
                        )
                    }
                }

                Messages(
                    messages = messages,
                    actions = chatActions,
                    modifier = modifier.weight(1f),
                    scrollState = scrollState,
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Surface(
                        tonalElevation = 5.dp
                    ) {
                        UserInput(
                            modifier = Modifier
                                .align(Alignment.BottomCenter),
                            textContent = {
                                UserTextInput(
                                    value = userInputValue,
                                    onValueChange = { userInputValue = it },
                                    sendAction = {
                                        viewModel.sendTaskChatMessage(it)
                                        userInputValue = userInputValue.copy("")
                                        scope.launch {
                                            delay(200)
                                            scrollState.animateScrollToItem(index = 0)
                                        }
                                    },
                                    actionsContent = {
                                        IconButton(
                                            onClick = {
                                                pickImage.launch(
                                                    PickVisualMediaRequest(
                                                        PickVisualMedia.ImageOnly
                                                    )
                                                )
                                            },
                                        ) {
                                            Icon(
                                                Icons.Filled.PhotoLibrary,
                                                contentDescription = "select images"
                                            )
                                        }
                                    }
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskChatEditor(
    isComplete: Boolean,
    taskTitle: () -> String,
    onTitleChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    toggleTaskComplete: () -> Unit = {},
    scheduleOptions: @Composable ColumnScope.() -> Unit = {},
) {
    var isEditing by remember { mutableStateOf(false) }
    val isKeyboardOpen by keyboardAsState()
    val focusManager = LocalFocusManager.current


    LaunchedEffect(isKeyboardOpen) {
        if (!isKeyboardOpen) {
            focusManager.clearFocus()
            isEditing = false
        }
    }

    BackHandler(isEditing) {
        if (isEditing) {
            focusManager.clearFocus()
            isEditing = false
        }
    }

    Surface(
        modifier = modifier,
        tonalElevation = 5.dp,
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .animateContentSize(animationSpec = tween())
                .then(
                    if (isEditing) {
                        Modifier.fillMaxHeight(0.7f)
                    } else {
                        Modifier.height(IntrinsicSize.Min)
                    }
                ),
        ) {

            val toggleModifier by rememberUpdatedState(
                Modifier.then(
                    if (isEditing) {
                        Modifier.alpha(0.5f)
                    } else {
                        Modifier
                            .alpha(1f)
                            .clickable(onClick = toggleTaskComplete)
                    }
                )
            )

            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {

                Row {

                    Box(
                        Modifier
                            .padding(start = 12.dp, top = 4.dp, end = 18.dp)
                            .size(32.dp)
                    ) {
                        AnimatedContent(
                            targetState = isComplete,
                            label = "Complete toggle animation",
                            transitionSpec = { scaleIn() togetherWith scaleOut() },
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            when (it) {
                                true -> {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = stringResource(R.string.cd_schedule_icon),
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .then(toggleModifier)
                                    )
                                }

                                false -> {
                                    Icon(
                                        imageVector = Icons.Outlined.Circle,
                                        contentDescription = stringResource(R.string.cd_schedule_icon),
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .then(toggleModifier)
                                    )
                                }
                            }
                        }
                    }

                    BasicTextField(
                        value = taskTitle(),
                        onValueChange = onTitleChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isEditing = focusState.isFocused
                            },
                        enabled = !isComplete,
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
                            textDecoration = when (isComplete) {
                                true -> TextDecoration.LineThrough
                                false -> null
                            }
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = !isEditing,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    scheduleOptions()
                }
            }
        }
    }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleButton(
    planModel: PlanModel,
    schedule: ScheduleDomain?,
    onTimeChange: (Int, Int) -> Unit,
    onDateChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    createPlanSchedule: () -> Unit,
    cancelActiveSchedule: () -> Unit,
) {

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    if (showDatePickerDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = planModel.epochMs
        )

        PriorityDatePicker(
            dismissDialog = { showDatePickerDialog = false },
            onDateChange = {
                datePickerState.selectedDateMillis?.let {
                    onDateChange(it)
                }
                showDatePickerDialog = false
                showTimePickerDialog = true

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
            dismissTimePicker = { showTimePickerDialog = false },
            onTimeChange = {
                onTimeChange(
                    timePickerState.hour,
                    timePickerState.minute
                )
                createPlanSchedule()
                showTimePickerDialog = false
            },
            state = timePickerState
        )
    }

    Row(modifier = modifier) {

        AnimatedContent(targetState = schedule, label = "") {
            when (it) {
                null -> {
                    PriorityButton(
                        onClick = { showDatePickerDialog = true },
                        text = stringResource(id = R.string.task_chat_schedule_reminder),
                        shape = RoundedCornerShape(percent = 50),
                    )
                }

                else -> {
                    PriorityButton(
                        onClick = { showDatePickerDialog = true },
                    ) {
                        Text(
                            text = planModel.displayDate,
                            style = TextStyle(
                                fontSize = 12.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFFFFFFFF),
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    PriorityButton(
                        onClick = { showTimePickerDialog = true },
                    ) {
                        Text(
                            text = planModel.displayTime,
                            style = TextStyle(
                                fontSize = 12.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFFFFFFFF),
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

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
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskChatTopAppBar(
    onBackPressed: () -> Unit,
) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_navigate_back)
                )
            }
        }
    )
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
fun TaskChatTopBarPreview() {
    MoreStuffTheme {
        TaskChatEditor(
            isComplete = false,
            taskTitle = { "Hello There this should be a very long text so that we can test how it looks" },
            onTitleChange = {}
        )
    }
}

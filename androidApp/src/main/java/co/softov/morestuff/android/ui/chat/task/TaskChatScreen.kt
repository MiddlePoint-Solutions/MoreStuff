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
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
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
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.nav.ChatScreen
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.chat.Messages
import co.softov.morestuff.android.ui.compose.ProvideLocalViewModelStoreOwner
import co.softov.morestuff.android.ui.compose.keyboardAsState
import co.softov.morestuff.android.ui.home.PlanModel
import co.softov.morestuff.android.ui.image.ImageImportScreen
import co.softov.morestuff.android.ui.image.ImagePreviewScreen
import co.softov.morestuff.android.ui.input.UserInput
import co.softov.morestuff.android.ui.input.UserTextInput
import co.softov.morestuff.android.ui.navigation.ChildStack
import co.softov.morestuff.android.ui.priority.PriorityButton
import co.softov.morestuff.android.ui.priority.PriorityDatePicker
import co.softov.morestuff.android.ui.priority.PriorityTimePicker
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.google.accompanist.insets.ui.Scaffold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

@Composable
fun TaskChatScreen(
    taskId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val navigation = remember { StackNavigation<ChatScreen>() }

    val lifecycleOwner = LocalView.current.findViewTreeLifecycleOwner()
    ProvideLocalViewModelStoreOwner(LifecycleViewModelStoreOwner(lifecycleOwner)) {

        val viewModel: TaskChatViewModel = koinViewModel { parametersOf(taskId) }

        val shareImage by rememberUpdatedState<(String) -> Unit> { imagePath ->
            viewModel.shareImage(imagePath)
        }

        ChildStack(
            source = navigation,
            initialStack = { listOf(ChatScreen.TaskChat) },
            handleBackButton = true,
            animation = stackAnimation(scale() + fade()),
        ) { screen ->
            when (screen) {
                is ChatScreen.TaskChat -> {

                    val task by viewModel.task.collectAsStateWithLifecycle()
                    val messages by viewModel.messages.collectAsStateWithLifecycle()

                    val chatActions = ChatActions(
                        scheduleAction = viewModel::scheduleResponse,
                        copyMessage = { message ->
                            viewModel.copyToClipboard(message.content)
                        },
                        deleteMessage = { message ->
                            viewModel.deleteMessage(messageId = message.id)
                        },
                        onImageSelected = {
                            val path = it.messageData?.filePath ?: ""
                            val title = it.content
                            navigation.push(ChatScreen.ImagePreview(path, title))
                        }
                    )

                    TaskChatContent(
                        task = task,
                        planModel = { viewModel.planModel },
                        messages = messages,
                        chatActions = chatActions,
                        modifier = modifier,
                        onBack = onBack,
                        sendTaskMessage = viewModel::sendTaskChatMessage,
                        updateTaskTitle = viewModel::updateTaskTitle,
                        toggleTaskComplete = viewModel::toggleTaskComplete,
                        updatePlanTime = viewModel::updatePlanTime,
                        updatePlanDate = viewModel::updatePlanDate,
                        createPlanSchedule = viewModel::createOneTimeSchedule,
                        cancelActiveSchedule = viewModel::cancelActiveSchedule,
                        imagePicked = {
                            navigation.push(ChatScreen.ImageImport(it.toString()))
                        }
                    )
                }

                is ChatScreen.ImageImport -> {
                    ImageImportScreen(
                        imageUri = Uri.parse(screen.uri),
                        send = { message ->
                            viewModel.sendImageMessageForTask(screen.uri, message)
                            navigation.pop()
                        },
                        onBack = navigation::pop
                    )
                }

                is ChatScreen.ImagePreview -> {
                    ImagePreviewScreen(
                        imagePath = screen.imagePath,
                        onBack = navigation::pop,
                        onSendImage = shareImage,
                        title = screen.title,
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskChatContent(
    task: TaskDomain,
    planModel: () -> PlanModel?,
    messages: List<Message>,
    chatActions: ChatActions,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    sendTaskMessage: (String) -> Unit = {},
    updateTaskTitle: (String) -> Unit = {},
    toggleTaskComplete: () -> Unit = {},
    updatePlanTime: (Int, Int) -> Unit = { _, _ -> },
    updatePlanDate: (Long) -> Unit = {},
    createPlanSchedule: () -> Unit = {},
    cancelActiveSchedule: () -> Unit = {},
    imagePicked: (Uri) -> Unit = {}
) {

    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    val pickImage = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            imagePicked(uri)
        } else {
            Timber.d("Image picker uri is NULL!")
        }
    }

    var userInputValue by rememberSaveable(
        key = task.id.toString(),
        stateSaver = TextFieldValue.Saver
    ) {
        mutableStateOf(TextFieldValue())
    }

    Scaffold(
        topBar = {
            TaskTopAppBar(
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
                    taskTitle = { task.title },
                    onTitleChange = updateTaskTitle,
                    toggleTaskComplete = toggleTaskComplete,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_schedule),
                            contentDescription = stringResource(R.string.cd_schedule_icon),
                        )

                        Spacer(modifier = Modifier.width(13.dp))

                        TaskSchedule(
                            planModel = planModel(),
                            onTimeChange = updatePlanTime,
                            onDateChange = updatePlanDate,
                            createPlanSchedule = createPlanSchedule,
                            cancelActiveSchedule = cancelActiveSchedule,
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
                                        sendTaskMessage(it)
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
private fun TaskChatEditor(
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
        focusManager.clearFocus()
        isEditing = false
    }

    Surface(
        modifier = modifier,
        tonalElevation = 5.dp,
    ) {
        Box(
            modifier = Modifier
                .animateContentSize(animationSpec = tween())
                .padding(vertical = 16.dp)
                .then(
                    if (isEditing) {
                        Modifier
                            .fillMaxHeight(0.7f)
                    } else {
                        Modifier.height(IntrinsicSize.Max)
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
                            transitionSpec = { scaleIn() togetherWith fadeOut() },
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
                        minLines = 2,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskSchedule(
    planModel: PlanModel?,
    onTimeChange: (Int, Int) -> Unit,
    onDateChange: (Long) -> Unit,
    createPlanSchedule: () -> Unit,
    cancelActiveSchedule: () -> Unit,
) {

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    AnimatedContent(
        targetState = planModel,
        label = "",
        contentKey = { it != null }
    ) { plan ->
        when (plan) {
            null -> {
                PriorityButton(
                    onClick = createPlanSchedule,
                    text = stringResource(id = R.string.task_chat_schedule_reminder),
                    shape = RoundedCornerShape(percent = 50),
                )
            }

            else -> {
                Row {
                    if (showDatePickerDialog) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = plan.epochMs
                        )

                        PriorityDatePicker(
                            dismissDialog = { showDatePickerDialog = false },
                            onDateChange = {
                                datePickerState.selectedDateMillis?.let {
                                    onDateChange(it)
                                }
                                showDatePickerDialog = false
                            },
                            state = datePickerState,
                        )
                    }

                    if (showTimePickerDialog) {
                        val timePickerState = rememberTimePickerState(
                            initialHour = plan.hour,
                            initialMinute = plan.minute
                        )
                        PriorityTimePicker(
                            dismissTimePicker = { showTimePickerDialog = false },
                            onTimeChange = {
                                onTimeChange(
                                    timePickerState.hour,
                                    timePickerState.minute
                                )
                                showTimePickerDialog = false
                            },
                            state = timePickerState
                        )
                    }

                    Button(
                        onClick = { showDatePickerDialog = true },
                        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = plan.displayDate,
                            style = TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFFFFFFFF),
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = { showTimePickerDialog = true },
                        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = plan.displayTime,
                            style = TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFFFFFFFF),
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { cancelActiveSchedule() },
                        modifier = Modifier
                    ) {
                        Icon(
                            Icons.Sharp.Close,
                            contentDescription = stringResource(R.string.cd_cancel_schedule),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskTopAppBar(
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

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
fun TaskSchedulePreview() {
    MoreStuffTheme {
        TaskSchedule(
            planModel = PlanModel(
                localDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
                displayDate = "Saturday, July 29",
                displayTime = "15:30"
            ),
            onDateChange = {},
            onTimeChange = { _, _ -> },
            cancelActiveSchedule = {},
            createPlanSchedule = {}
        )
    }
}

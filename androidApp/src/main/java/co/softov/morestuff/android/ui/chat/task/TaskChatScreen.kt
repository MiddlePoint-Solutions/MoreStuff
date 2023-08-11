package co.softov.morestuff.android.ui.chat.task

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.nav.ChatScreen
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.chat.Messages
import co.softov.morestuff.android.ui.compose.ProvideLocalViewModelStoreOwner
import co.softov.morestuff.android.ui.home.ScheduleUiModel
import co.softov.morestuff.android.ui.image.ImageImportScreen
import co.softov.morestuff.android.ui.image.ImagePreviewScreen
import co.softov.morestuff.android.ui.input.UserInput
import co.softov.morestuff.android.ui.input.UserTextInput
import co.softov.morestuff.android.ui.navigation.ChildStack
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
    ProvideLocalViewModelStoreOwner(lifecycleOwner) {

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
                        },
                        shareImage = { imagePath -> shareImage(imagePath) }
                    )

                    TaskChatContent(
                        task = task,
                        taskTitle = { viewModel.taskTitle },
                        schedule = { viewModel.scheduleModel },
                        reminder = { viewModel.reminderModel },
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
    taskTitle: () -> String,
    chatActions: ChatActions,
    modifier: Modifier = Modifier,
    messages: List<MessageWithFormattedTime> = listOf(),
    onBack: () -> Unit = {},
    schedule: () -> ScheduleUiModel? = { null },
    reminder: () -> ScheduleUiModel? = { null },
    sendTaskMessage: (String) -> Unit = {},
    updateTaskTitle: (String) -> Unit = {},
    toggleTaskComplete: () -> Unit = {},
    updatePlanTime: (Int, Int) -> Unit = { _, _ -> },
    updatePlanDate: (Long) -> Unit = {},
    createPlanSchedule: () -> Unit = {},
    cancelActiveSchedule: () -> Unit = {},
    imagePicked: (Uri) -> Unit = {},
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
                .animateContentSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                TaskChatEditor(
                    isComplete = task.isComplete,
                    taskTitle = taskTitle,
                    onTitleChange = updateTaskTitle,
                    toggleTaskComplete = toggleTaskComplete,
                ) {

                    TaskSchedule(
                        model = schedule,
                        actionText = stringResource(id = R.string.task_chat_schedule_action),
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_schedule),
                                contentDescription = stringResource(R.string.cd_schedule_icon),
                            )
                        },
                        onTimeChange = updatePlanTime,
                        onDateChange = updatePlanDate,
                        createSchedule = createPlanSchedule,
                        cancelSchedule = cancelActiveSchedule,
                    )

                    // TODO: uncomment this when we have proper support for reminders
                    /*TaskSchedule(
                        model = reminder,
                        actionText = stringResource(R.string.task_chat_reminder_action),
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_reminder),
                                contentDescription = stringResource(R.string.cd_reminder_icon),
                            )
                        },
                        onTimeChange = updatePlanTime,
                        onDateChange = updatePlanDate,
                        createSchedule = createPlanSchedule,
                        cancelSchedule = cancelActiveSchedule,
                    )*/

                }

                Messages(
                    messages = messages,
                    actions = chatActions,
                    modifier = modifier.weight(1f),
                    scrollState = scrollState,
                )

                AnimatedVisibility(visible = !task.isComplete) {
                    TaskChatInput(
                        userInputValue = userInputValue,
                        onValueChange = { userInputValue = it },
                        sendTaskMessage = {
                            sendTaskMessage(it)
                            userInputValue = userInputValue.copy("")
                            scope.launch {
                                delay(200)
                                scrollState.animateScrollToItem(index = 0)
                            }
                        },
                        pickImage = {
                            pickImage.launch(
                                PickVisualMediaRequest(
                                    PickVisualMedia.ImageOnly
                                )
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskChatInput(
    userInputValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    sendTaskMessage: (String) -> Unit,
    pickImage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Surface {
            UserInput(
                modifier = Modifier.align(Alignment.BottomCenter),
                backgroundColor = MaterialTheme.colorScheme.background,
                textContent = {
                    UserTextInput(
                        value = userInputValue,
                        onValueChange = onValueChange,
                        sendAction = sendTaskMessage,
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer ,
                        actionsContent = {
                            IconButton(
                                onClick = pickImage,
                            ) {
                                Icon(
                                    Icons.Filled.PhotoLibrary,
                                    contentDescription = stringResource(R.string.cd_select_images)
                                )
                            }
                        },
                    )
                },

            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskTopAppBar(
    onBackPressed: () -> Unit,
) {
    Surface {
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_navigate_back)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
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
fun TaskChatPreview() {
    MoreStuffTheme {
        TaskChatContent(
            task = TaskDomain(),
            taskTitle = { "This is TaskChat!" },
            schedule = {
                ScheduleUiModel(
                    localDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
                    displayDate = "Saturday, July 29",
                    displayTime = "15:30"
                )
            },
            chatActions = ChatActions(),
        )
    }
}

package co.softov.morestuff.android.ui.chat.task

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.nav.ChatScreen
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.chat.Messages
import co.softov.morestuff.android.ui.components.SendIcon
import co.softov.morestuff.android.ui.image.ImageImportScreen
import co.softov.morestuff.android.ui.image.ImagePreviewScreen
import co.softov.morestuff.android.ui.input.LocalBoxWeight
import co.softov.morestuff.android.ui.input.UserInput
import co.softov.morestuff.android.ui.input.UserTextInput
import co.softov.morestuff.android.ui.input.VoiceToTextInput
import co.softov.morestuff.android.ui.model.MessageUiModel
import co.softov.morestuff.android.ui.model.ScheduleUiModel
import co.softov.morestuff.android.ui.navigation.ChildStack
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
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
    val viewModel: TaskChatViewModel = koinViewModel(
        key = "TaskChat$taskId",
        parameters = { parametersOf(taskId) }
    )
    val shareImage by rememberUpdatedState<(String) -> Unit> { imagePath ->
        viewModel.shareImage(imagePath)
    }


    ChildStack(
        source = navigation,
        initialStack = { listOf(ChatScreen.TaskChat) },
        key = "TaskChatStack",
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
                    shareImage = { imagePath -> shareImage(imagePath) },
                    shareMessage = viewModel::shareMessage
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
                    },

                )
            }

            is ChatScreen.ImageImport -> {
                ImageImportScreen(
                    imageUri = Uri.parse(screen.uri),
                    onImport = { title ->
                        viewModel.sendImageMessageForTask(screen.uri, title)
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

@Composable
private fun TaskChatContent(
    task: TaskDomain,
    taskTitle: () -> String,
    chatActions: ChatActions,
    modifier: Modifier = Modifier,
    messages: List<MessageUiModel> = listOf(),
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

    Scaffold(
        topBar = {
            TaskTopAppBar(
                onBackPressed = onBack,
            )
        },
        modifier = modifier.navigationBarsPadding(),
        containerColor = Color.Transparent,
    ) { scaffoldPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(top = scaffoldPadding.calculateTopPadding())
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

                AnimatedVisibility(
                    visible = !task.isComplete,
                    modifier = Modifier.background(Color.Transparent)
                ) {
                    TaskChatInput(
                        sendTaskMessage = {
                            sendTaskMessage(it)
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent),
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskChatInput(
    sendTaskMessage: (String) -> Unit,
    pickImage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isTextEmpty = remember { mutableStateOf(true) }

    var userInputValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }

    Box(
        modifier = modifier
    ) {
        UserInput(
            modifier = Modifier.align(Alignment.BottomCenter),
            backgroundColor = Color.Transparent,
            textContent = {
                val weight = if (isTextEmpty.value) 0.30f else 0.12f
                CompositionLocalProvider(LocalBoxWeight provides weight) {
                    UserTextInput(
                        value = userInputValue,
                        onValueChange = {
                            userInputValue = it
                            isTextEmpty.value = it.text.isBlank()
                        },
                        sendAction = {
                            sendTaskMessage(it)
                            userInputValue = userInputValue.copy(text = "")
                        },
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        actionsContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (isTextEmpty.value) {
                                    IconButton(
                                        onClick = pickImage,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            Icons.Filled.PhotoLibrary,
                                            contentDescription = stringResource(R.string.cd_select_images)
                                        )
                                    }
                                    VoiceToTextInput(
                                        onUpdateValue = {
                                            userInputValue = userInputValue.copy(text = it)
                                            isTextEmpty.value = it.isBlank()
                                        }
                                    )
                                } else {
                                    AnimatedVisibility(
                                        visible = !isTextEmpty.value,
                                        enter = fadeIn(),
                                        exit = fadeOut()
                                    ) {
                                        SendIcon(onClick = {
                                            sendTaskMessage(userInputValue.text)
                                            userInputValue = TextFieldValue()
                                            isTextEmpty.value = true
                                        })
                                    }
                                }
                            }
                        },
                    )
                }
            },
        )
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
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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

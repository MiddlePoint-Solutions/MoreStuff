package io.middlepoint.morestuff.shared.ui.screen.chat.task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import io.github.xxfast.decompose.router.stack.RoutedContent
import io.github.xxfast.decompose.router.stack.Router
import io.github.xxfast.decompose.router.stack.rememberRouter
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.nav.ChatScreen
import io.middlepoint.morestuff.shared.domain.nav.ChatScreen.ImageImport
import io.middlepoint.morestuff.shared.domain.nav.ChatScreen.ImagePreview
import io.middlepoint.morestuff.shared.domain.nav.ChatScreen.TaskChat
import io.middlepoint.morestuff.shared.ui.components.ConfirmDeleteDialog
import io.middlepoint.morestuff.shared.ui.components.SendIcon
import io.middlepoint.morestuff.shared.ui.components.input.LocalBoxWeight
import io.middlepoint.morestuff.shared.ui.components.input.UserInput
import io.middlepoint.morestuff.shared.ui.components.input.UserTextInput
import io.middlepoint.morestuff.shared.ui.components.input.voice.VoiceToTextInput
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import io.middlepoint.morestuff.shared.ui.screen.chat.ChatActions
import io.middlepoint.morestuff.shared.ui.screen.chat.Messages
import io.middlepoint.morestuff.shared.ui.screen.chat.items.AppChatItem
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.CopyText
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.DeleteMessage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.DeleteTask
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.InputDocument
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.InputImage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.InputText
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.OpenDocument
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ScheduleResponse
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ShareDocument
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ShareImage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ShareMessage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ToggleTaskComplete
import io.middlepoint.morestuff.shared.ui.screen.image.ImageImportScreen
import io.middlepoint.morestuff.shared.ui.screen.image.ImagePreviewScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import morestuff.shared.generated.resources.Res
import morestuff.shared.generated.resources.cd_more_options
import morestuff.shared.generated.resources.cd_navigate_back
import morestuff.shared.generated.resources.cd_select_images
import morestuff.shared.generated.resources.cd_task_done_icon
import morestuff.shared.generated.resources.cd_undo
import morestuff.shared.generated.resources.complete
import morestuff.shared.generated.resources.delete
import morestuff.shared.generated.resources.restore
import morestuff.shared.generated.resources.select_image
import morestuff.shared.generated.resources.select_pdf
import morestuff.shared.generated.resources.task_chat_complete_message_with_date
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.random.Random

@Composable
fun TaskChatScreen(
    taskId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val router: Router<ChatScreen> = rememberRouter(ChatScreen::class) {
        listOf(TaskChat)
    }

    val viewModel: TaskChatPresenter = koinViewModel(
        key = "TaskChat$taskId",
        parameters = { parametersOf(taskId) }
    )

    RoutedContent(
        router = router,
        modifier = Modifier.background(Color.Transparent),
        animation = stackAnimation(scale() + fade()),
    ) { screen ->

        when (screen) {
            is TaskChat -> {

                val model by viewModel.models.collectAsState()

                val chatActions = remember {
                    ChatActions(
                        scheduleAction = { scheduleId, replyType ->
                            viewModel.take(ScheduleResponse(scheduleId, replyType))
                        },
                        copyMessage = { viewModel.take(CopyText(it.content)) },
                        deleteMessage = { viewModel.take(DeleteMessage(it)) },
                        onImageSelected = {
                            val path = it.messageData?.filePath ?: ""
                            val title = it.content
                            router.push(ImagePreview(path, title))
                        },
                        onPdfSelected = {
                            val path = it.messageData?.filePath ?: ""
                            viewModel.take(OpenDocument(path))
                        },
                        shareImage = { viewModel.take(ShareImage(it)) },
                        sharePdf = { viewModel.take(ShareDocument(it)) },
                        shareMessage = { viewModel.take(ShareMessage(it)) }
                    )
                }

                TaskChatContent(
                    model = model,
                    onEvent = viewModel::take,
                    chatActions = chatActions,
                    modifier = modifier,
                    onBack = onBack,
                    sendTaskMessage = { viewModel.take(InputText(it)) },
                    imagePicked = { router.push(ImageImport(it.toString())) },
                    pdfPicked = { uri ->
                        viewModel.take(InputDocument(uri.toString(), title = ""))
                    }
                )
            }

            is ImageImport -> {
                ImageImportScreen(
                    imageUri = screen.uri,
                    onImport = { title ->
                        viewModel.take(InputImage(screen.uri, title))
                        router.pop()
                    },
                    onBack = router::pop
                )
            }

            is ImagePreview -> {
                ImagePreviewScreen(
                    imagePath = screen.imagePath,
                    onBack = router::pop,
                    onSendImage = { viewModel.take(ShareImage(screen.imagePath)) },
                    title = screen.title,
                )
            }
        }
    }
}

@Composable
private fun TaskChatContent(
    model: TaskChatState,
    chatActions: ChatActions,
    onEvent: (TaskChatEvent) -> Unit,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    sendTaskMessage: (String) -> Unit = {},
    imagePicked: (String) -> Unit = {},
    pdfPicked: (String) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    val task = model.task
    val messages = model.messages

    // TODO: Image picker
//    val pickImage = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
//        if (uri != null) {
//            imagePicked(uri)
//        } else {
//            Logger.d("Image picker uri is NULL!")
//        }
//    }
//    // TODO: PDF picker
//    val pickPdf = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//        if (uri != null) {
//            pdfPicked(uri)
//        } else {
//            Logger.d("Pdf picker uri is NULL!")
//        }
//    }

    Scaffold(
        topBar = {
            TaskTopAppBar(
                isComplete = task.isComplete,
                onBack = onBack,
                onDelete = { showDeleteConfirmationDialog = true },
                onToggleComplete = { onEvent(ToggleTaskComplete) }
            )
        },
        modifier = modifier.navigationBarsPadding(),
        containerColor = Color.Transparent,
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = scaffoldPadding.calculateTopPadding())
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                Messages(
                    messages = messages,
                    actions = chatActions,
                    modifier = modifier.weight(1f),
                    scrollState = scrollState,
                    contentPadding = PaddingValues(top = 40.dp, bottom = 20.dp)
                )

                AnimatedVisibility(
                    visible = task.isComplete,
                    modifier = Modifier.background(Color.Transparent)
                ) {
                    AppChatItem(
                        message = MessageUiModel(
                            id = Random.nextLong(),
                            taskId = task.id,
                            scheduleId = 0L,
                            contentType = ContentType.APP_TASK_MESSAGE,
                            createTime = "",
                            content = stringResource(
                                Res.string.task_chat_complete_message_with_date,
                                task.completeTime
                            ),
                            formattedTime = "",
                            formattedTimeOnly = ""
                        ),
                        chatActions
                    )
                    Spacer(modifier = Modifier.padding(bottom = 80.dp))
                }

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
//                            pickImage.launch(
//                                PickVisualMediaRequest(
//                                    PickVisualMedia.ImageOnly
//                                )
//                            )
                        },
                        pickPdf = {
//                            pickPdf.launch("application/pdf")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent),
                    )
                }
            }

            TaskDetails(
                taskId = task.id,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    if (showDeleteConfirmationDialog) {
        ConfirmDeleteDialog(
            onDismiss = { showDeleteConfirmationDialog = false },
            onConfirm = {
                onEvent(DeleteTask)
                onBack()
                showDeleteConfirmationDialog = false
            }
        )
    }
}


@Composable
private fun TaskChatInput(
    sendTaskMessage: (String) -> Unit,
    pickImage: () -> Unit,
    pickPdf: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isTextEmpty = remember { mutableStateOf(true) }

    var userInputValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        UserInput(
            modifier = Modifier.align(Alignment.BottomCenter),
            textContent = {
                val weight = if (isTextEmpty.value) 0.30f else 0.12f
                CompositionLocalProvider(LocalBoxWeight provides weight) {
                    UserTextInput(
                        value = userInputValue,
                        onValueChange = {
                            userInputValue = it
                            isTextEmpty.value = it.text.isBlank()
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
                                        onClick = { showMenu = true },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            Icons.Filled.AttachFile,
                                            contentDescription = stringResource(Res.string.cd_select_images)
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = showMenu,
                                        onDismissRequest = { showMenu = false }
                                    ) {
                                        DropdownMenuItem(onClick = {
                                            pickImage()
                                            showMenu = false
                                        },
                                            text = {
                                                Text(
                                                    text = stringResource(Res.string.select_image)
                                                )
                                            })
                                        DropdownMenuItem(onClick = {
                                            pickPdf()
                                            showMenu = false
                                        },
                                            text = {
                                                Text(
                                                    text = stringResource(Res.string.select_pdf)
                                                )
                                            })

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
                                            userInputValue = userInputValue.copy(text = "")
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
    isComplete: Boolean,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit,
) {
    Surface {
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.cd_navigate_back)
                    )
                }
            },
            actions = {

                var showMenu by remember { mutableStateOf(false) }

                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(Res.string.cd_more_options)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {

                    DropdownMenuItem(
                        text = {
                            if (isComplete) {
                                Text(stringResource(Res.string.restore))
                            } else {
                                Text(stringResource(Res.string.complete))
                            }

                        },
                        onClick = {
                            onToggleComplete()
                            showMenu = false
                        },
                        leadingIcon = {
                            if (isComplete) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Undo,
                                    contentDescription = stringResource(Res.string.cd_undo)
                                )
                            } else {
                                Icon(
                                    Icons.Filled.Done,
                                    contentDescription = stringResource(Res.string.cd_task_done_icon)
                                )
                            }
                        }
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.delete)) },
                        onClick = {
                            onDelete()
                            showMenu = false
                        },
                        leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) }
                    )
                }


            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    name = "Dark"
//)
//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_NO,
//    name = "Light"
//)
//@Composable
//fun TaskChatPreview() {
//    MoreStuffTheme {
//        TaskChatContent(
//            model = TaskChatState(),
//            chatActions = ChatActions(),
//            onEvent = {},
//        )
//    }
//}

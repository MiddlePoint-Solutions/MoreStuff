package io.middlepoint.morestuff.shared.ui.screen.schedule.iaScope

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.router.stack.push
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.middlepoint.morestuff.shared.domain.nav.ChatScreen.ImageImport
import io.middlepoint.morestuff.shared.domain.nav.ChatScreen.ImagePreview
import io.middlepoint.morestuff.shared.domain.service.logger
import io.middlepoint.morestuff.shared.ui.screen.chat.ChatActions
import io.middlepoint.morestuff.shared.ui.screen.chat.task.EditingMessageReference
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.CopyText
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.DeleteMessage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.InputDocument
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.InputText
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.OpenDocument
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ScheduleResponse
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ShareDocument
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ShareImage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ShareMessage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatInput
import io.middlepoint.morestuff.shared.ui.screen.settings.koinInjectOnRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IAScopeContent(
    scopeId: Long,
/*    chatActions: ChatActions,
    sendMessage: (String) -> Unit,
    imagePicked: (PlatformFile) -> Unit = {},
    pdfPicked: (PlatformFile) -> Unit = {},
    isAIEnabled: Boolean = false,*/
    modifier: Modifier = Modifier,
) {

    val viewModel = koinInjectOnRoute(
        type = IaScopePresenter::class,
        parameters = { parametersOf(scopeId) }
    )

    val model = viewModel.models.collectAsState()
    val messages = model.value.messages
    val isAIEnabled = model.value.isAIEnabled


    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val focusManager = LocalFocusManager.current

    var editingMessageId by remember { mutableStateOf<Long?>(null) }
    var editingMessageContent by remember { mutableStateOf<String?>(null) }

    val editingMessage = remember(editingMessageId, messages) {
        messages.find { it.id == editingMessageId }
    }

    val singleImagePickerLauncher = rememberFilePickerLauncher(
        type = FileKitType.Image,
    ) { files ->
        //files?.let { viewModel.take(IAChatEvent.ImageImport(it)) }
    }

    val singleFilePickerLauncher = rememberFilePickerLauncher(
        type = FileKitType.File(extensions = listOf("pdf", "docx"))
    ) { files ->
        files?.let { viewModel.take(IAChatEvent.InputDocument(it, title = "")) }
    }

    val chatActions = remember {
        ScopeChatActions(
            scheduleAction = { scheduleId, replyType ->
                //viewModel.take(ScheduleResponse(scheduleId, replyType))
            },
            copyMessage = { viewModel.take(IAChatEvent.CopyText(it.content)) },
            deleteMessage = { viewModel.take(IAChatEvent.DeleteMessage(it)) },
            onImageSelected = {
                val path = it.messageData?.filePath ?: ""
                val title = it.content
                //router.push(ImagePreview(path, title))
            },
            onPdfSelected = {
                val path = it.messageData?.filePath ?: ""
                viewModel.take(IAChatEvent.OpenDocument(path))
            },
            shareImage = { /*viewModel.take(IAChatEvent.ShareImage(it))*/ },
            sharePdf = {/* viewModel.take(IAChatEvent.ShareDocument(it))*/ },
            shareMessage = { /*viewModel.take(IAChatEvent.ShareMessage(it))*/ },
            setEditingMessage = { messageId ->
                //viewModel.take(IAChatEvent.SetEditingMessage(messageId))
            },
            updateMessageContent = { content ->
                //viewModel.take(IAChatEvent.UpdateMessageContent(content))
            },
            isMessageBeingEdited = { false/*messageId ->
                model.editingMessageId == messageId*/
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { focusManager.clearFocus() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            logger.d { "Messages: $messages" }
            ScopeMessages(
                messages = messages,
                actions = chatActions,
                modifier = modifier.weight(1f),
                scrollState = scrollState,
                contentPadding = PaddingValues(top = 24.dp, bottom = 20.dp),
            )

            AnimatedVisibility(
                visible = true, // Siempre visible en IA
                modifier = Modifier.background(Color.Transparent)
            ) {
                Column {
                    AnimatedVisibility(
                        visible = editingMessageId != null && editingMessage != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                       /* EditingMessageReference(
                            message = editingMessage,
                            onCancelEdit = { editingMessageId = null }
                        )*/
                    }
                    Spacer(Modifier.height(8.dp))
                    TaskChatInput(
                        sendTaskMessage = {
                            logger.d { "Sending task message: $it" }

                            viewModel.take(IAChatEvent.InputText(it))
                            viewModel.take(IAChatEvent.CreateAIMessage(it))
                            /*if (isAIEnabled) viewModel.take(IAChatEvent.CreateAIMessage(it))
                            coroutineScope.launch {
                                delay(200)
                                scrollState.animateScrollToItem(index = 0)
                            }*/
                        },
                        pickImage = { singleImagePickerLauncher.launch() },
                        pickPdf = { singleFilePickerLauncher.launch() },
                        editingMessageId = editingMessageId,
                        editingContent = editingMessageContent?:"",
                        onCancelEdit = { editingMessageId = null },
                        onUpdateMessage = { content -> editingMessageContent = content },
                        isAIEnabled = isAIEnabled,
                        onToggleAI = { /* tu lógica aquí */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent),
                    )
                }
            }
        }
    }
}
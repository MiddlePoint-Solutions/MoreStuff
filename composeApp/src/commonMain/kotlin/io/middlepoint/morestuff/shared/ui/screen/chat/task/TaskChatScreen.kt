package io.middlepoint.morestuff.shared.ui.screen.chat.task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.ui.components.CreateScopeBottomSheet
import io.middlepoint.morestuff.shared.ui.components.DeleteBottomSheet
import io.middlepoint.morestuff.shared.ui.components.SendIcon
import io.middlepoint.morestuff.shared.ui.components.input.LocalBoxWeight
import io.middlepoint.morestuff.shared.ui.components.input.UserInput
import io.middlepoint.morestuff.shared.ui.components.input.UserTextInput
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import io.middlepoint.morestuff.shared.ui.screen.chat.ChatActions
import io.middlepoint.morestuff.shared.ui.screen.chat.Messages
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ActivateAI
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.CancelEditingMessage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.CreateNewScopeForTask
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.CreateTaskCompletionMessage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.DeleteMessage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.DeleteTask
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.MoveTaskToScope
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ToggleTaskComplete
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.UpdateMessageContent
import io.middlepoint.morestuff.shared.ui.screen.home.ScopeSelectionBottomSheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.cancel
import morestuff.composeapp.generated.resources.cd_more_options
import morestuff.composeapp.generated.resources.cd_navigate_back
import morestuff.composeapp.generated.resources.cd_task_done_icon
import morestuff.composeapp.generated.resources.cd_undo
import morestuff.composeapp.generated.resources.complete
import morestuff.composeapp.generated.resources.confirm_delete
import morestuff.composeapp.generated.resources.delete
import morestuff.composeapp.generated.resources.edit_message
import morestuff.composeapp.generated.resources.restore
import morestuff.composeapp.generated.resources.task_chat_complete_message
import morestuff.composeapp.generated.resources.task_schedule_deletion_warning_singular
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskChatContent(
  model: TaskChatState,
  chatActions: ChatActions,
  onEvent: (TaskChatEvent) -> Unit,
  modifier: Modifier = Modifier,
  onBack: () -> Unit = {},
  sendTaskMessage: (String) -> Unit = {},
  imagePicked: (PlatformFile) -> Unit = {},
  pdfPicked: (PlatformFile) -> Unit = {},
  logger: Logger = koinInject(),
  isAIEnabled: Boolean = false
) {
  val coroutineScope = rememberCoroutineScope()
  val scrollState = rememberLazyListState()

  val deleteSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val scopeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var showDeleteBottomSheet by remember { mutableStateOf(false) }
  var showScopeSelection by remember { mutableStateOf(false) }

  var showCreateScopeSheet by remember { mutableStateOf(false) }
  val createScopeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val task = model.task
  val scope = model.scope
  val messages = model.messages
  val editingMessageId = model.editingMessageId
  val editingMessageContent = model.editingMessageContent
  val allScopes = model.scopes
  val isAILoading = model.isAILoading
  val focusManager = LocalFocusManager.current
  var titleLineCount by remember { mutableStateOf(0) }

  val editingMessage = remember(editingMessageId, messages) {
    messages.find { it.id == editingMessageId }
  }

  val singleImagePickerLauncher = rememberFilePickerLauncher(
    type = FileKitType.Image,
  ) { files ->
    files?.let {
      logger.d { "Image path: $it" }
      imagePicked(it)
    }
  }

  val singleFilePickerLauncher = rememberFilePickerLauncher(
    type = FileKitType.File(extensions = listOf("pdf", "docx"))
  ) { files ->
    files?.let {
      logger.d { "PDF path: $it" }
      pdfPicked(it)
    }
  }

  val completionMessage = stringResource(Res.string.task_chat_complete_message)
  val prevIsComplete = remember { mutableStateOf(task.isComplete) }

  LaunchedEffect(task.isComplete) {
    if (prevIsComplete.value != task.isComplete) {

      if (task.isComplete && task.completeTime.isNotEmpty()) {
        val alreadyPosted = messages.any { msg ->
          msg.contentType == ContentType.APP_TASK_MESSAGE &&
              msg.content.startsWith(completionMessage)
        }

        if (!alreadyPosted) {
          delay(500)
          onEvent(
            CreateTaskCompletionMessage(
              content = completionMessage
            )
          )
        }

      } else if (!task.isComplete) {
        messages
          .filter { msg ->
            msg.contentType == ContentType.APP_TASK_MESSAGE &&
                msg.content.startsWith(completionMessage)
          }
          .forEach { msg ->
            onEvent(DeleteMessage(msg))
          }
      }
      prevIsComplete.value = task.isComplete
    }
  }

  val aiMessages = remember(messages) {
    messages.filter { it.contentType == ContentType.AI_TASK_MESSAGE }
  }

  LaunchedEffect(aiMessages) {
    logger.d { "AI Messages: ${aiMessages.size}" }
    aiMessages.forEach { message ->
      logger.d { "AI Message: id=${message.id}, content='${message.content}'" }
    }
  }

  val scopeName = scope.name

  Scaffold(
    topBar = {
      TaskTopAppBar(
        isComplete = task.isComplete,
        onBack = onBack,
        onDelete = { showDeleteBottomSheet = true },
        onToggleComplete = { onEvent(ToggleTaskComplete) },
        labelText = scopeName,
        onLabelClick = { showScopeSelection = true }
      )
    },
    containerColor = MaterialTheme.colorScheme.surfaceContainer,
  ) { scaffoldPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .clickable(
          indication = null,
          interactionSource = remember { MutableInteractionSource() }
        ) { focusManager.clearFocus() }
        .padding(top = scaffoldPadding.calculateTopPadding())
    ) {
      Column(
        modifier = Modifier.fillMaxSize()
      ) {
        val contentPadding = if (titleLineCount > 1)
          PaddingValues(top = 74.dp, bottom = 20.dp)
        else
          PaddingValues(top = 40.dp, bottom = 20.dp)

        Messages(
          messages = messages,
          actions = chatActions,
          modifier = modifier.weight(1f),
          scrollState = scrollState,
          contentPadding = contentPadding,
          isAILoading = isAILoading,
        )
        AnimatedVisibility(
          visible = !task.isComplete,
          modifier = Modifier.background(Color.Transparent)
        ) {
          Column {
            AnimatedVisibility(
              visible = editingMessageId != null && editingMessage != null,
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
            ) {
              EditingMessageReference(
                message = editingMessage,
                onCancelEdit = { onEvent(CancelEditingMessage) }
              )
            }
            Spacer(modifier.height(8.dp))
            TaskChatInput(
              sendTaskMessage = {
                sendTaskMessage(it)
                coroutineScope.launch {
                  delay(200)
                  scrollState.animateScrollToItem(index = 0)
                }
              },
              pickImage = { singleImagePickerLauncher.launch() },
              pickPdf = { singleFilePickerLauncher.launch() },
              enabled = !showCreateScopeSheet,
              editingMessageId = editingMessageId,
              editingContent = editingMessageContent,
              onCancelEdit = {
                onEvent(CancelEditingMessage)
              },
              onUpdateMessage = { content ->
                onEvent(UpdateMessageContent(content))
              },
              //isAIEnabled = isAIEnabled,
              onToggleAI = { onEvent(ActivateAI) },
              modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent),
            )
          }
        }
      }

      TaskDetails(
        taskId = task.id,
        modifier = Modifier.align(Alignment.TopCenter),
        onTitleLineCount = { count ->
          titleLineCount = count
        }
      )
    }
  }

  if (showDeleteBottomSheet) {
    val taskHasSchedule = if (task.hasSchedule) {
      stringResource(Res.string.task_schedule_deletion_warning_singular)
    } else {
      null
    }
    DeleteBottomSheet(
      sheetState = deleteSheetState,
      onDismissRequest = { showDeleteBottomSheet = false },
      title = stringResource(Res.string.confirm_delete),
      message = task.title,
      confirmButtonText = stringResource(Res.string.delete),
      dismissButtonText = stringResource(Res.string.cancel),
      onConfirm = {
        onEvent(DeleteTask)
        onBack()
        showDeleteBottomSheet = false
      },
      extraInfo = taskHasSchedule
    )
  }
  if (showScopeSelection) {
    ScopeSelectionBottomSheet(
      onDismissRequest = {
        coroutineScope.launch {
          scopeSheetState.hide()
          showScopeSelection = false
        }
      },
      scopes = allScopes,
      sheetState = scopeSheetState,
      addSelectedTasksToScope = { scopeId ->
        onEvent(MoveTaskToScope(scopeId))
      },
      createNewScope = {
        showCreateScopeSheet = true
      }
    )
  }

  if (showCreateScopeSheet) {
    CreateScopeBottomSheet(
      sheetState = createScopeSheetState,
      onDismissRequest = {
        coroutineScope.launch {
          createScopeSheetState.hide()
          showCreateScopeSheet = false
        }
      },
      onConfirm = { scopeName ->
        onEvent(CreateNewScopeForTask(scopeName))
        coroutineScope.launch {
          createScopeSheetState.hide()
          showCreateScopeSheet = false
        }
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
  enabled: Boolean = true,
  editingMessageId: Uuid? = null,
  editingContent: String = "",
  onCancelEdit: () -> Unit = {},
  onUpdateMessage: (String) -> Unit = {},
  //isAIEnabled: Boolean = false,
  onToggleAI: () -> Unit = {}
) {
  val isTextEmpty = remember { mutableStateOf(editingContent.isEmpty()) }
  val isRecording = remember { mutableStateOf(false) }
  val showSendIcon = remember { mutableStateOf(editingContent.isNotEmpty()) }
  val focusRequester = remember { FocusRequester() }
  val keyboardController = LocalSoftwareKeyboardController.current

  var userInputValue by rememberSaveable(
    stateSaver = TextFieldValue.Saver,
    inputs = arrayOf(editingMessageId, editingContent)
  ) {
    mutableStateOf(
      if (editingMessageId != null)
        TextFieldValue(editingContent, TextRange(editingContent.length))
      else
        TextFieldValue("")
    )
  }

  LaunchedEffect(editingMessageId, editingContent) {
    if (editingMessageId != null) {
      userInputValue = TextFieldValue(editingContent, TextRange(editingContent.length))
      isTextEmpty.value = editingContent.isEmpty()
      showSendIcon.value = !isTextEmpty.value
      focusRequester.requestFocus()
      keyboardController?.show()
    } else {
      userInputValue = TextFieldValue("")
      isTextEmpty.value = true
      showSendIcon.value = false
    }
  }

  var showMenu by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
  ) {
    UserInput(
      modifier = Modifier.align(Alignment.BottomCenter),
      enablePadding = enabled,
      textContent = {
        val weight = if (isTextEmpty.value) 0.30f else 0.12f
        CompositionLocalProvider(LocalBoxWeight provides weight) {
          UserTextInput(
            value = userInputValue,
            //isAIEnabled = isAIEnabled,
            onValueChange = {
              userInputValue = it
              isTextEmpty.value = it.text.isBlank()
              showSendIcon.value = !isTextEmpty.value
              if (editingMessageId != null) {
                onUpdateMessage(it.text)
              }
            },
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.focusRequester(focusRequester),
            /*   leadingContent = {
                 IconButton(
                   onClick = onToggleAI,
                 ) {
                   Crossfade(
                     targetState = isAIEnabled,
                     animationSpec = tween(
                       durationMillis = 300,
                       easing = FastOutSlowInEasing
                     )
                   ) { aiEnabled ->
                     Icon(
                       painter = if (aiEnabled)
                         painterResource(Res.drawable.ic_ai_enabled)
                       else
                         painterResource(Res.drawable.ic_ai_disabled),
                       contentDescription = "Toggle AI",
                       tint = Color.Unspecified
                     )
                   }
                 }
               },*/
            actionsContent = {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
              ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                  SendIcon(
                    onClick = {
                      if (editingMessageId != null) {
                        onUpdateMessage(userInputValue.text)
                        onCancelEdit()
                      } else {
                        sendTaskMessage(userInputValue.text)
                        userInputValue = TextFieldValue("")
                        isTextEmpty.value = true
                        showSendIcon.value = false
                      }
                    },
                    enabled = userInputValue.text.isNotBlank()
                  )
                  /*this@Row.AnimatedVisibility(
                    visible = isTextEmpty.value && editingMessageId == null,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                  ) {
                    IconButton(
                      onClick = { showMenu = true }
                    ) {
                      Icon(
                        Icons.Filled.AttachFile,
                        contentDescription = stringResource(Res.string.cd_select_images)
                      )
                    }
                  }*/

                  /* this@Row.AnimatedVisibility(
                     visible = !isTextEmpty.value || editingMessageId != null,
                     enter = fadeIn() + scaleIn(),
                     exit = fadeOut() + scaleOut()
                   ) {
                     SendIcon(onClick = {
                       if (editingMessageId != null) {
                         onUpdateMessage(userInputValue.text)
                         onCancelEdit()
                       } else {
                         sendTaskMessage(userInputValue.text)
                         userInputValue = TextFieldValue("")
                         isTextEmpty.value = true
                         showSendIcon.value = false
                       }
                     })
                   }*/
                }
              }

//              DropdownMenu(
//                expanded = showMenu,
//                onDismissRequest = { showMenu = false }
//              ) {
//                DropdownMenuItem(
//                  onClick = {
//                    pickImage()
//                    showMenu = false
//                  },
//                  text = {
//                    Text(
//                      text = stringResource(Res.string.select_image)
//                    )
//                  })
//                DropdownMenuItem(
//                  onClick = {
//                    pickPdf()
//                    showMenu = false
//                  },
//                  text = {
//                    Text(
//                      text = stringResource(Res.string.select_pdf)
//                    )
//                  })
//              }
              /*VoiceToTextInput(
                onUpdateValue = {
                  userInputValue = userInputValue.copy(text = it)
                  showSendIcon.value = it.isNotBlank()
                  isTextEmpty.value = it.isBlank()
                },
                onRecordingStateChanged = { recording ->
                  isRecording.value = recording
                  if (!recording && userInputValue.text.isNotBlank()) {
                    showSendIcon.value = true
                  }
                },
                isHomeScreen = true
              )*/
            },
          )
        }
      },
    )
  }
}


@Composable
fun EditingMessageReference(
  message: MessageUiModel?,
  onCancelEdit: () -> Unit
) {
  if (message == null) return

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
      Column(
        modifier = Modifier.weight(1f)
      ) {
        Text(
          text = stringResource(Res.string.edit_message),
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.primary
        )
      }

      IconButton(
        onClick = onCancelEdit,
        modifier = Modifier.size(32.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = stringResource(Res.string.cancel),
          tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskTopAppBar(
  isComplete: Boolean,
  onBack: () -> Unit,
  onDelete: () -> Unit,
  onToggleComplete: () -> Unit,
  labelText: String,
  onLabelClick: () -> Unit
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
        Surface(
          onClick = onLabelClick,
          enabled = !isComplete,
          color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12F),
          shape = RoundedCornerShape(16.dp),

          ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = labelText,
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.secondary
            )
          }
        }
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
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
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

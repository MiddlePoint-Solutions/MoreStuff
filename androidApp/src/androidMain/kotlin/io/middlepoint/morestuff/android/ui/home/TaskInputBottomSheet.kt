package io.middlepoint.morestuff.android.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ModeStandby
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import io.middlepoint.morestuff.android.R
import io.middlepoint.morestuff.shared.domain.model.ChatContext
import io.middlepoint.morestuff.shared.domain.nav.Screen
import io.middlepoint.morestuff.android.ui.chat.ChatActions
import io.middlepoint.morestuff.android.ui.chat.Messages
import io.middlepoint.morestuff.android.ui.components.ScopeCarousel
import io.middlepoint.morestuff.android.ui.components.SendIcon
import io.middlepoint.morestuff.android.ui.input.UserInput
import io.middlepoint.morestuff.android.ui.input.UserInputEvent
import io.middlepoint.morestuff.android.ui.input.UserInputViewModel
import io.middlepoint.morestuff.android.ui.input.UserTextInput
import io.middlepoint.morestuff.android.ui.input.voice.VoiceToTextInput
import io.middlepoint.morestuff.android.ui.local.LocalAppRouter
import io.middlepoint.morestuff.android.ui.model.PriorityUiModel
import io.middlepoint.morestuff.android.ui.priority.PriorityInput
import com.arkivanov.decompose.router.stack.push
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TaskInputBottomSheet(
  onDismissRequest: () -> Unit,
  sheetState: SheetState,
  context: ChatContext,
  onNewTaskCreated: (taskId: Long, priority: PriorityUiModel) -> Unit,
  viewModel: UserInputViewModel = koinViewModel()
) {

  val coroutineScope = rememberCoroutineScope()
  val focusRequester = remember { FocusRequester() }
  val scrollState = rememberLazyListState()
  val navigation = LocalAppRouter.current

  LaunchedEffect(context) {
    viewModel.take(UserInputEvent.LoadContext(context))
  }

  val chatActions = remember {
    ChatActions(
      taskChatAction = {
        navigation.push(Screen.TaskChat(it))
        coroutineScope.launch {
          sheetState.hide()
        }
      }
    )
  }

  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    sheetState = sheetState,
    shape = RectangleShape,
    dragHandle = null,
    content = {

      val state by viewModel.models.collectAsState()

      val messages = state.messages
      val priority = state.priority
      val schedule = state.planTime
      val scopes = state.scopes

      val onTaskCreated by remember(state.lastCreatedTaskId) {
        mutableStateOf(state.lastCreatedTaskId)
      }

      LaunchedEffect(onTaskCreated) {
        Timber.d("LaunchedEffect: $onTaskCreated")
        onTaskCreated?.let { taskId ->
          onNewTaskCreated(taskId, priority)
        }
      }

      Box(
        modifier = Modifier.fillMaxWidth()
      ) {

        Surface(
          modifier = Modifier.fillMaxWidth(),
          tonalElevation = 8.dp,
          shadowElevation = 2.dp
        ) {
          ScopeCarousel(
            scopes = scopes,
            currentScopeId = context.scopeId,
            onScopeSelected = { viewModel.take(UserInputEvent.SetCurrentScope(it)) },
            modifier = Modifier
              .height(60.dp)
              .fillMaxWidth()
          )
        }

        Surface(
          modifier = Modifier.align(Alignment.CenterStart),
          shape = RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50),
          tonalElevation = 10.dp,
          shadowElevation = 2.dp
        ) {
          Icon(
            imageVector = Icons.Filled.ModeStandby,
            contentDescription = stringResource(R.string.cd_scopes_icon),
            modifier = Modifier
              .size(38.dp)
              .padding(start = 4.dp)
          )
        }
      }

      ConstraintLayout {

        val (chat, input) = createRefs()

        Messages(
          messages = messages,
          actions = chatActions,
          modifier = Modifier
            .fillMaxWidth()
            .constrainAs(chat) {
              top.linkTo(parent.top)
              bottom.linkTo(input.top)
              height = Dimension.preferredWrapContent
            },
          scrollState = scrollState,
          contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp)
        )

        Surface(
          modifier = Modifier.constrainAs(input) {
            bottom.linkTo(parent.bottom, margin = 6.dp)
          }
        ) {
          var userInputValue by rememberSaveable(
            stateSaver = TextFieldValue.Saver,
            key = "UserInput1"
          ) {
            mutableStateOf(TextFieldValue(text = ""))
          }

          val isTextEmpty = remember(userInputValue.text) {
            mutableStateOf(userInputValue.text.isBlank())
          }

          UserInput(
            priorityContent = {
              PriorityInput(
                priority = priority,
                schedule = schedule,
                onEvent = viewModel::take,
                modifier = Modifier.fillMaxWidth()
              )
            },
            textContent = {
              Surface(
                color = MaterialTheme.colorScheme.surfaceVariant
              ) {
                UserTextInput(
                  value = userInputValue,
                  onValueChange = { userInputValue = it },
                  focusRequester = focusRequester,
                  startWithFocus = true,
                  actionsContent = {
                    if (isTextEmpty.value) {
                      VoiceToTextInput(
                        onUpdateValue = {
                          userInputValue = userInputValue.copy(it)
                        },
                      )
                    } else {
                      this@ModalBottomSheet.AnimatedVisibility(
                        visible = !isTextEmpty.value,
                        enter = fadeIn(),
                        exit = fadeOut()
                      ) {
                        SendIcon(
                          onClick = {
                            viewModel.take(UserInputEvent.CreateNewTask(userInputValue.text))
                            coroutineScope.launch {
                              userInputValue = userInputValue.copy("")
                            }
                          }
                        )
                      }
                    }
                  }
                )
              }
            }
          )
        }
      }
    }
  )
}

package co.softov.morestuff.android.ui.home

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.ChatContext
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.chat.Messages
import co.softov.morestuff.android.ui.components.ScopeCarousel
import co.softov.morestuff.android.ui.components.SendIcon
import co.softov.morestuff.android.ui.input.UserInput
import co.softov.morestuff.android.ui.input.UserInputViewModel
import co.softov.morestuff.android.ui.input.UserTextInput
import co.softov.morestuff.android.ui.input.voice.VoiceToTextInput
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import co.softov.morestuff.android.ui.model.PriorityUiModel
import co.softov.morestuff.android.ui.priority.PriorityInput
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

    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val priorityModel by viewModel.priorityModel.collectAsStateWithLifecycle()
    val scopes by viewModel.scopes.collectAsState()

    LaunchedEffect(Unit) {
        Timber.d("TaskInputBottomSheet: ${context.scopeId}")
        viewModel.load(context)
    }

    val navigation = LocalAppNavigation.current

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
                        onScopeSelected = viewModel::setCurrentScope,
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
                                model = priorityModel,
                                onEvent = viewModel::onEvent,
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
                                                SendIcon(onClick = {
                                                    coroutineScope.launch {
                                                        val taskId =
                                                            viewModel.createNewTask(userInputValue.text)
                                                        onNewTaskCreated(
                                                            taskId,
                                                            priorityModel.priority
                                                        )
                                                        userInputValue = userInputValue.copy("")
                                                    }
                                                })
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

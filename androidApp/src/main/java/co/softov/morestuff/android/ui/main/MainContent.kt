package co.softov.morestuff.android.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import co.softov.morestuff.android.app.util.LifecycleEventsObserver
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.chat.Messages
import co.softov.morestuff.android.ui.input.UserInput
import co.softov.morestuff.android.ui.priority.PriorityInput
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val viewModel: MainViewModel = getViewModel()

    LifecycleEventsObserver(
        onResume = { viewModel.onResume() }
    )

    val chatActions = ChatActions(
        scheduleAction = viewModel::scheduleResponse,
        taskChatAction = viewModel::showTaskChat,
    )


    val messageItems = viewModel.messages.collectAsState()
    Surface(modifier) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    Messages(
                        messages = messageItems.value,
                        actions = chatActions,
                        modifier = Modifier.weight(1f),
                        scrollState = scrollState
                    )
                    PriorityInput(
                        model = Priority.Now(),
                        onPriorityChange = viewModel::priorityChanged,
                    )

                    UserInput(
                        modifier = Modifier.imePadding(),
                        showTaskListAction = viewModel::showTaskList,
                        onMessageSent = { content ->
                            viewModel.addNewTask(content)
                        },
                        resetScroll = {
                            scope.launch {
                                //scrollState.scrollToItem(index = 0)
                            }
                        }
                    )
                }
            }
        }
    }
}


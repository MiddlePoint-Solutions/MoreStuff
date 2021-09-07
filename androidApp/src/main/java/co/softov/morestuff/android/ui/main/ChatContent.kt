package co.softov.morestuff.android.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.model.Message
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.navigationBarsWithImePadding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ChatContent(
    conductor: ContentConductor
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val viewModel = getViewModel<ContentViewModel> {
        parametersOf(conductor)
    }

    val chatActions = ChatActions(
        scheduleAction = viewModel::scheduleResponse,
        confirmationAction = { _, _ -> }
    )

    Surface(Modifier.navigationBarsPadding(bottom = false)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Messages(
                messages = viewModel.messagePager,
                actions = chatActions,
                modifier = Modifier.weight(1f),
                scrollState = scrollState
            )

            UserInput(
                viewModel = viewModel,
                onMessageSent = { content ->
                    viewModel.addNewTask(content)
                },
                resetScroll = {
                    scope.launch {
                        scrollState.scrollToItem(0)
                    }
                },
                modifier = Modifier.navigationBarsWithImePadding()
            )

        }
    }
}

@Composable
private fun Messages(
    messages: Flow<PagingData<Message>>,
    actions: ChatActions,
    modifier: Modifier = Modifier,
    scrollState: LazyListState
) {

    val lazyPagingItems = messages.collectAsLazyPagingItems()

    Box(modifier = modifier) {
        LazyColumn(
            reverseLayout = true,
            modifier = Modifier.fillMaxSize(),
            state = scrollState
        ) {
            items(
                lazyPagingItems,
                key = { item -> item.id }
            ) { item ->
                when (item?.contentType) {
                    ContentType.USER_NEW_TASK -> UserChatItem(message = item)
                    ContentType.CONFIRM_NEW_TASK,
                    ContentType.TASK_REMINDER -> AppChatItem(message = item, actions = actions)
                }
            }
        }
    }
}

@Composable
private fun UserInput(
    viewModel: ContentViewModel,
    modifier: Modifier = Modifier,
    onMessageSent: (String) -> Unit,
    resetScroll: () -> Unit
) {
    val state by viewModel.priorityState.collectAsState()

    Column(
        modifier = modifier
    ) {
        UserPriorityInput(
            currentPriority = state
        ) {
            when (it) {
                is Priority.Later -> viewModel.selectedLaterPriority()
                is Priority.Today -> viewModel.selectedTodayPriority()
                is Priority.Tomorrow -> viewModel.selectedTomorrowPriority()
            }
        }

        UserTextInput(
            listAction = viewModel::showTaskList,
            sendAction = {
                onMessageSent(it)
                resetScroll()
            }
        )
    }
}
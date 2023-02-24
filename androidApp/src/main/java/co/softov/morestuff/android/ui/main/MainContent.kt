package co.softov.morestuff.android.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import co.softov.morestuff.android.app.util.LifecycleEventsObserver
import co.softov.morestuff.android.ui.main.chat.ChatActions
import co.softov.morestuff.android.ui.main.chat.Messages
import co.softov.morestuff.android.ui.main.input.UserInput
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MainContent(
    conductor: MainConductor,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val viewModel = getViewModel<MainViewModel> {
        parametersOf(conductor)
    }

    LifecycleEventsObserver(
        onResume = { viewModel.onResume() }
    )

    val chatActions = ChatActions(
        scheduleAction = viewModel::scheduleResponse,
        confirmationAction = { _, _ -> }
    )

    val messageItems = viewModel.messages.collectAsState()

    Surface(modifier.systemBarsPadding()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Messages(
                    messages = messageItems.value,
                    actions = chatActions,
                    modifier = Modifier.weight(1f),
                    scrollState = scrollState
                )
            }

            UserInput(
                modifier = Modifier.imePadding(),
                viewModel = viewModel,
                onMessageSent = { content ->
                    viewModel.addNewTask(content)
                },
                resetScroll = {
                    scope.launch {
                        //scrollState.scrollToItem(index = 0)
                    }
                },
            )
        }
    }
}
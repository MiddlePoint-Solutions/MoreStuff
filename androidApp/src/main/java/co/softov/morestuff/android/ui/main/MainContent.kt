package co.softov.morestuff.android.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import app.cash.molecule.RecompositionClock
import app.cash.molecule.launchMolecule
import co.softov.morestuff.android.app.util.LifecycleEventsObserver
import co.softov.morestuff.android.presentation.presenter.PriorityOptionsPresenter
import co.softov.morestuff.android.presentation.presenter.PriorityPresenter
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.chat.Messages
import co.softov.morestuff.android.ui.input.UserInput
import co.softov.morestuff.android.ui.priority.PriorityInput
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

    val priority by scope.launchMolecule(clock = RecompositionClock.ContextClock) {
        PriorityPresenter()
    }.collectAsState()

    val priorityOptions by scope.launchMolecule(clock = RecompositionClock.ContextClock) {
        PriorityOptionsPresenter()
    }.collectAsState()

    val chatActions = ChatActions(
        scheduleAction = viewModel::scheduleResponse,
        taskChatAction = viewModel::showTaskChat,
    )

    val messageItems = viewModel.messages.collectAsState()
    Surface(modifier) {
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
                priority, priorityOptions
            )

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
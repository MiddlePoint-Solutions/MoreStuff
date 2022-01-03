package co.softov.morestuff.android.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.navigationBarsWithImePadding
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

    val messageItems = viewModel.tasks.collectAsState()

    Surface(Modifier.navigationBarsPadding(bottom = false)) {
        Column(
            modifier = Modifier
            .fillMaxSize()
        ) {

            PriorityLayout(
                modifier = Modifier.padding(4.dp),
            ) {
                messageItems.value.shuffled().forEach {
                    PriorityTask(it.title)
                }
            }

            /*Messages(
                messages = messageItems.value,
                actions = chatActions,
                modifier = Modifier.weight(1f),
                scrollState = scrollState
            )*/

            UserInput(
                viewModel = viewModel,
                onMessageSent = { content ->
                    viewModel.addNewTask(content)
                },
                resetScroll = {
                    scope.launch {
                        //scrollState.scrollToItem(index = 0)
                    }
                },
                modifier = Modifier.navigationBarsWithImePadding()
            )
        }
    }
}
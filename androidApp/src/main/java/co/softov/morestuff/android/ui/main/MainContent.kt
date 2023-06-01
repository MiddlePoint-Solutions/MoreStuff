package co.softov.morestuff.android.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.app.util.LifecycleEventsObserver
import co.softov.morestuff.android.ui.chat.Messages
import co.softov.morestuff.android.ui.chat.TaskActions
import co.softov.morestuff.android.ui.input.UserInput
import co.softov.morestuff.android.ui.priority.PriorityInput
import co.softov.morestuff.android.ui.schedule.PriorityContent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainContent(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val viewModel: MainViewModel = koinViewModel()

    LifecycleEventsObserver(
        onResume = { viewModel.onResume() }
    )

    val messages by viewModel.messages.collectAsState()
    var showChat by remember { mutableStateOf(false) }

    val chatActions = remember {
        TaskActions(
            scheduleAction = viewModel::scheduleResponse,
            taskChatAction = viewModel::showTaskChat,
        )
    }

    Surface {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                Modifier.fillMaxSize()
            ) {
                MoreStuffSystemChat(onClick = { showChat = !showChat })

                if (showChat) {
                    Messages(
                        messages = messages,
                        actions = chatActions,
                        modifier = Modifier.weight(1f),
                        scrollState = scrollState
                    )
                } else {
                    PriorityContent(
                        snackbarHostState = snackbarHostState,
                        modifier = Modifier.weight(1f),
                        listState = scrollState
                    )
                }

                Column(modifier.navigationBarsPadding()) {
                    PriorityInput(
                        priority = viewModel.priorityModel,
                        planModel = viewModel.planModel,
                        onPriorityChange = viewModel::priorityChanged,
                        onTimeChange = viewModel::updatePlanTime,
                        onDateChange = viewModel::updatePlanDate,
                    )

                    UserInput(
                        modifier = Modifier
                            .imePadding(),
                        showTaskListAction = viewModel::showTaskList,
                        onMessageSent = { content ->
                            viewModel.addNewTask(content)
                        },
                        resetScroll = {
                            scope.launch {
                                delay(200)
                                scrollState.animateScrollToItem(index = 0)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MoreStuffSystemChat(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 18.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Image(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(56.dp)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically),
                painter = rememberVectorPainter(image = Icons.Default.Flare),
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(56.dp)
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Mr. Stuff",
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}


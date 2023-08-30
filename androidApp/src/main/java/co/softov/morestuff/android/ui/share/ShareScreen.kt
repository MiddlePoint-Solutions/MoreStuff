package co.softov.morestuff.android.ui.share

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.nav.Shareable
import co.softov.morestuff.android.ui.home.mapToDomain
import co.softov.morestuff.android.ui.input.UserInput
import co.softov.morestuff.android.ui.input.UserInputViewModel
import co.softov.morestuff.android.ui.input.UserTextInput
import co.softov.morestuff.android.ui.input.VoiceToTextInput
import co.softov.morestuff.android.ui.priority.PriorityInput
import co.softov.morestuff.android.ui.schedule.PriorityItem
import co.softov.morestuff.android.ui.schedule.TaskProfile
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.surfaceContainer
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
    onBack: () -> Unit,
    shareable: Shareable,
    shareToExistingTask: (taskId: Long, shareable: Shareable) -> Unit,
) {

    Scaffold(
        topBar = {
            Surface(shadowElevation = 5.dp) {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.select_chat)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_navigate_back)
                            )
                        }
                    },
                    actions = {
                        // TODO: Uncomment below code when implementing search for share.
                        /* IconButton(onClick = { *//* TODO: search *//* }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.cd_search_chats),
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }*/
                    }
                )
            }
        },
        content = {
            ShareContent(
                shareToTask = { taskId -> shareToExistingTask(taskId, shareable) },
                shareable = shareable,
                modifier = Modifier.padding(it)
            )
        },
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareContent(
    shareable: Shareable,
    shareToTask: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
    shareViewModel: ShareViewModel = koinViewModel(),
    userInputViewModel: UserInputViewModel = koinViewModel()
) {

    val state = rememberLazyListState()
    var showUserInput by remember { mutableStateOf(false) }

    var newTaskContent by remember { mutableStateOf<Pair<String, Priority>?>(null) }
    LaunchedEffect(newTaskContent) {
        newTaskContent?.let {
            val taskId = shareViewModel.createNewShareableTask(it.first, it.second)
            shareToTask(taskId)
        }
    }

    Box(modifier) {
        LazyColumn(
            state = state,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End
        ) {

            item {
                CreateNewTaskItem {
                    showUserInput = true
                }

                Divider(
                    thickness = 0.8.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            items(
                shareViewModel.tasks,
                key = { it.id }
            ) { task ->
                PriorityItem(
                    task = task,
                    onClick = shareToTask
                )

                Divider(
                    thickness = 0.8.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showUserInput) {

        val contentTitle = when (shareable) {
            is Shareable.Image -> ""
            is Shareable.Text -> shareable.content
        }

        var userInputValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(text = contentTitle))
        }

        val priorityModel by userInputViewModel.priorityModel.collectAsStateWithLifecycle()

        ModalBottomSheet(
            onDismissRequest = { showUserInput = false },
            modifier = Modifier.imePadding(),
            dragHandle = null,
            shape = RoundedCornerShape(0),
            content = {
                UserInput(
                    priorityContent = {
                        PriorityInput(
                            model = priorityModel,
                            onNowSelected = userInputViewModel::setNowPriority,
                            onLaterSelected = userInputViewModel::setLaterPriority,
                            onPlanSelected = userInputViewModel::setPlanPriority,
                            onTimeChange = userInputViewModel::updatePlanTime,
                            onDateChange = userInputViewModel::updatePlanDate,
                        )
                    },
                    textContent = {
                        UserTextInput(
                            value = userInputValue,
                            onValueChange = { userInputValue = it },
                            sendAction = {
                                newTaskContent = it to priorityModel.mapToDomain()
                            },
                            actionsContent = {
                                VoiceToTextInput(
                                    onUpdateValue = userInputViewModel::updateUserInput
                                )
                            }
                        )
                    },
                )
            })
    }
}

@Composable
private fun CreateNewTaskItem(showUserInput: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .heightIn(80.dp)
            .padding(start = 14.dp)
            .clickable(onClick = showUserInput),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        TaskProfile(title = "+")

        Text(
            text = stringResource(R.string.create_new_chat),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        )
    }
}

@Preview
@Composable
fun ShareContentPreview() {
    MoreStuffTheme() {
        ShareContent(
            shareable = Shareable.Text(""),
            shareToTask = {},
        )
    }
}
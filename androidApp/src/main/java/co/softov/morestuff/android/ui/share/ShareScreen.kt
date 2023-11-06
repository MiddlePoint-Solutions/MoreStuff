package co.softov.morestuff.android.ui.share

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.nav.Shareable
import co.softov.morestuff.android.ui.input.UserInput
import co.softov.morestuff.android.ui.input.UserInputViewModel
import co.softov.morestuff.android.ui.input.UserTextInput
import co.softov.morestuff.android.ui.input.VoiceToTextInput
import co.softov.morestuff.android.ui.main.MainViewModel
import co.softov.morestuff.android.ui.model.mapToDomain
import co.softov.morestuff.android.ui.priority.PriorityInput
import co.softov.morestuff.android.ui.schedule.PriorityItem
import co.softov.morestuff.android.ui.schedule.TaskProfile
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.surfaceContainer
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
    onBack: () -> Unit,
    shareable: Shareable,
    shareToExistingTask: (taskId: Long, shareable: Shareable) -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.cd_search_chats),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
            }
        },
        content = {
            ShareContent(
                shareToTask = { taskId -> shareToExistingTask(taskId, shareable) },
                shareable = shareable,
                modifier = Modifier.padding(it),
                searchQuery = searchQuery,
            )
        },
    )

    AnimatedVisibility(
        visible = isSearchActive,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        ShareSearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onBack = {
                isSearchActive = false
                searchQuery = ""
            },
            shareable = shareable,
            shareToExistingTask = shareToExistingTask
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    shareable: Shareable,
    shareToExistingTask: (taskId: Long, shareable: Shareable) -> Unit,
) {

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    SearchBar(
        query = searchQuery,
        onQueryChange = onSearchQueryChange,
        onSearch = onSearchQueryChange,
        onActiveChange = { active -> if (!active) onBack() },
        modifier = Modifier.focusRequester(focusRequester),
        active = true,
        placeholder = {
            Text(text = stringResource(R.string.search))
        },
        leadingIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.cd_close_search)
                )
            }
        },
        content = {
            ShareContent(
                shareToTask = { taskId -> shareToExistingTask(taskId, shareable) },
                shareable = shareable,
                searchQuery = searchQuery,
                searchBarActive = true
            )
        },
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        )
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareContent(
    shareable: Shareable,
    shareToTask: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
    shareViewModel: ShareViewModel = koinViewModel(),
    userInputViewModel: UserInputViewModel = koinViewModel(),
    mainViewModel: MainViewModel = koinViewModel(),
    searchQuery: String,
    searchBarActive: Boolean = false,
) {
    val filteredTasks by shareViewModel.filteredTasks.collectAsState()

    val state = rememberLazyListState()
    var showUserInput by remember { mutableStateOf(false) }

    var newTaskContent by remember { mutableStateOf<Pair<String, Priority>?>(null) }
    LaunchedEffect(newTaskContent) {
        newTaskContent?.let {
            val taskId = shareViewModel.createNewShareableTask(it.first, it.second)
            shareToTask(taskId)
        }
    }
    LaunchedEffect(searchQuery) {
        shareViewModel.updateQuery(searchQuery)
    }

    Box(modifier) {
        LazyColumn(
            state = state,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End
        ) {
            if (!searchBarActive) {
                item {
                    CreateNewTaskItem {
                        mainViewModel.creatingNewTask.value = true
                        showUserInput = true
                    }

                    Divider(
                        thickness = 0.8.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            items(
                filteredTasks,
                key = { it.id }
            ) { task ->
                PriorityItem(
                    task = task,
                    onClick = { shareToTask(task.id) },
                    onLongClick = {},
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
            is Shareable.Text -> ""
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
            searchQuery = ""
        )
    }
}
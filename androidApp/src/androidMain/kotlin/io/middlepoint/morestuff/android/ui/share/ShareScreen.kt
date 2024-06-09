package io.middlepoint.morestuff.android.ui.share

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.middlepoint.morestuff.android.R
import io.middlepoint.morestuff.android.ui.home.TaskInputBottomSheet
import io.middlepoint.morestuff.android.ui.schedule.PriorityItem
import io.middlepoint.morestuff.android.ui.schedule.TaskProfile
import io.middlepoint.morestuff.android.ui.share.ShareEvent.*
import io.middlepoint.morestuff.shared.ui.theme.MoreStuffTheme
import io.middlepoint.morestuff.shared.domain.model.ChatContext
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
    onBack: () -> Unit,
    shareable: Shareable,
    viewModel: ShareViewModel = koinViewModel(),
    shareToExistingTask: (taskId: Long, shareable: Shareable) -> Unit,
) {
    var isSearchActive by remember { mutableStateOf(false) }

    val state by viewModel.models.collectAsState()

    Scaffold(
        topBar = {
            Surface(shadowElevation = 5.dp) {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.select_chat)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                shareable = shareable,
                tasks = state.tasks,
                shareToTask = { taskId -> shareToExistingTask(taskId, shareable) },
                modifier = Modifier.padding(it),
            )
        },
    )

    AnimatedVisibility(
        visible = isSearchActive,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {

        var searchQuery by remember(isSearchActive) { mutableStateOf("") }

        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            snapshotFlow { searchQuery }
                .onEach { viewModel.take(UpdateSearchQuery(it)) }
                .onCompletion { viewModel.take(ClearSearchQuery) }
                .collect()
        }

        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = { searchQuery = it },
            onActiveChange = { active ->
                if (!active) {
                    isSearchActive = false
                }
            },
            modifier = Modifier
                .navigationBarsPadding()
                .focusRequester(focusRequester),
            active = true,
            placeholder = { Text(text = stringResource(R.string.search)) },
            leadingIcon = {
                IconButton(
                    onClick = {
                        isSearchActive = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.cd_close_search)
                    )
                }
            },
            content = {
                Crossfade(
                    targetState = state.searchResults,
                    animationSpec = tween(durationMillis = 150),
                    label = "Search results crossfade"
                ) {
                    ShareTasksList(
                        searchBarActive = true,
                        tasks = it,
                        shareToTask = { taskId -> shareToExistingTask(taskId, shareable) },
                    )
                }

            },
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareContent(
    shareable: Shareable,
    tasks: List<TaskUiModel>,
    shareToTask: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
    searchBarActive: Boolean = false,
) {

    val scope = rememberCoroutineScope()
    var showUserInput by remember { mutableStateOf(false) }

    ShareTasksList(
        modifier = modifier,
        searchBarActive = searchBarActive,
        tasks = tasks,
        shareToTask = shareToTask,
        showUserInput = { showUserInput = true }
    )

    if (showUserInput) {
        val taskInputBottomSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        val chatContext = remember { ChatContext.Share(shareable) }

        BackHandler(onBack = {
            scope.launch {
                taskInputBottomSheetState.hide()
            }
        })

        TaskInputBottomSheet(
            onDismissRequest = { showUserInput = false },
            sheetState = taskInputBottomSheetState,
            context = chatContext,
            onNewTaskCreated = { taskId, _ ->
                scope.launch {
                    delay(300)
                    taskInputBottomSheetState.hide()
                    shareToTask(taskId)
                }
            },
        )
    }
}

@Composable
private fun ShareTasksList(
    tasks: List<TaskUiModel>,
    searchBarActive: Boolean,
    shareToTask: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
    showUserInput: () -> Unit = {}
) {
    Box(modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End
        ) {
            if (!searchBarActive) {
                item {
                    CreateNewTaskItem(showUserInput)

                    Divider(
                        thickness = 0.8.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            items(
                tasks,
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
}

@Composable
private fun CreateNewTaskItem(showUserInput: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = showUserInput)
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .heightIn(80.dp)
            .padding(start = 14.dp),
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
            tasks = listOf(),
            shareToTask = {},
        )
    }
}
package io.middlepoint.morestuff.shared.ui.screen.share

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkivanov.essenty.backhandler.BackCallback
import io.github.xxfast.decompose.router.LocalRouterContext
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.ui.components.EmptyScopeContent
import io.middlepoint.morestuff.shared.ui.components.InputItem
import io.middlepoint.morestuff.shared.ui.components.PriorityItem
import io.middlepoint.morestuff.shared.ui.components.TaskProfile
import io.middlepoint.morestuff.shared.ui.extension.checkRegister
import io.middlepoint.morestuff.shared.ui.extension.checkUnregister
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel
import io.middlepoint.morestuff.shared.ui.screen.home.ScopeTabs
import io.middlepoint.morestuff.shared.ui.screen.schedule.ScopeContent
import io.middlepoint.morestuff.shared.ui.screen.schedule.ScopeTasksModels
import io.middlepoint.morestuff.shared.ui.screen.schedule.ScopeTasksViewModel
import io.middlepoint.morestuff.shared.ui.screen.settings.koinInjectOnRoute
import io.middlepoint.morestuff.shared.ui.screen.share.ShareEvent.ClearSearchQuery
import io.middlepoint.morestuff.shared.ui.screen.share.ShareEvent.UpdateSearchQuery
import io.middlepoint.morestuff.shared.ui.theme.surfaceContainerElevation
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.cd_close_search
import morestuff.composeapp.generated.resources.cd_navigate_back
import morestuff.composeapp.generated.resources.cd_search_chats
import morestuff.composeapp.generated.resources.create_new_chat
import morestuff.composeapp.generated.resources.search
import morestuff.composeapp.generated.resources.select_chat
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
  onBack: () -> Unit,
  shareable: Shareable,
  shareToExistingTask: (taskId: Long, shareable: Shareable) -> Unit,
) {

  var isSearchActive by remember { mutableStateOf(false) }

  val viewModel = koinInjectOnRoute(ShareViewModel::class, parameters = {
    parametersOf(shareable)
  })
  val state by viewModel.models.collectAsState()

  LaunchedEffect(state.createdTaskId) {
    state.createdTaskId?.let { shareToExistingTask(it, shareable) }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(text = stringResource(Res.string.select_chat)) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(Res.string.cd_navigate_back)
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surfaceContainerElevation,
        ),
        actions = {
          IconButton(onClick = { isSearchActive = true }) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = stringResource(Res.string.cd_search_chats),
              tint = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      )
    },
    content = {
      if (state.scopes.isNotEmpty()) {
        ShareContent(
          model = state,
          onEvent = viewModel::take,
          shareToTask = { taskId -> shareToExistingTask(taskId, shareable) },
          modifier = Modifier.padding(it),
        )
      }
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

    val onActiveChange: (Boolean) -> Unit = { active ->
      if (!active) {
        isSearchActive = false
      }
    }
    val colors = SearchBarDefaults.colors(
      containerColor = MaterialTheme.colorScheme.surfaceContainer,
    )
    SearchBar(
      inputField = {
        SearchBarDefaults.InputField(
          query = searchQuery,
          onQueryChange = { searchQuery = it },
          onSearch = { searchQuery = it },
          expanded = true,
          onExpandedChange = onActiveChange,
          placeholder = { Text(text = stringResource(Res.string.search)) },
          leadingIcon = {
            IconButton(
              onClick = {
                isSearchActive = false
              }
            ) {
              Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(Res.string.cd_close_search)
              )
            }
          },
          colors = TextFieldDefaults.colors(),
        )
      },
      expanded = true,
      onExpandedChange = onActiveChange,
      modifier = Modifier
        .navigationBarsPadding()
        .focusRequester(focusRequester),
      colors = colors,
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
    )
  }
}

@Composable
private fun ShareContent(
  model: ShareModel,
  onEvent: (ShareEvent) -> Unit,
  shareToTask: (taskId: Long) -> Unit,
  modifier: Modifier = Modifier,
) {

  val coroutineScope = rememberCoroutineScope()
  val taskInputActive = model.taskInputActive

  val states by rememberUpdatedState(newValue = model.scopes.map { rememberLazyListState() })
  val pagerState = rememberPagerState(pageCount = { model.scopes.size })
  var currentScopePage by remember { mutableIntStateOf(pagerState.currentPage) }
  val scopes by rememberUpdatedState(newValue = model.scopes)

  LaunchedEffect(Unit) {
    snapshotFlow { pagerState.currentPage }
      .collect { page ->
        if (currentScopePage != page) {
          launch { states[currentScopePage].animateScrollToItem(0) }
          currentScopePage = page
        }
        onEvent(ShareEvent.ScopeSelected(scopes[page].id))
      }
  }

  val backCallback = remember {
    BackCallback {
      onEvent(ShareEvent.ResetShareState)
    }
  }

  val backHandler = LocalRouterContext.current.backHandler
  LaunchedEffect(model) {
    if (taskInputActive) {
      backHandler.checkRegister(backCallback)
    } else {
      backHandler.checkUnregister(backCallback)
    }
  }

  Column(
    modifier = modifier
  ) {
    Row(
      modifier = Modifier
        .shadow(1.dp)
        .padding(bottom = 0.5.dp)
    ) {
      ScopeTabs(
        currentPage = pagerState.currentPage,
        scopes = model.scopes,
        onScopeSelected = { index, _ ->
          coroutineScope.launch {
            pagerState.animateScrollToPage(index)
          }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerElevation,
        createNewScope = {},
        isCreateScopeVisible = false
      )
    }

    AnimatedContent(
      targetState = taskInputActive,
      transitionSpec = { fadeIn().togetherWith(fadeOut()) }
    ) { active ->
      if (active) {
        InputItem(
          onDone = { title ->
            onEvent(ShareEvent.CreateNewTask(title))
          },
          onCancel = { onEvent(ShareEvent.ResetShareState) }
        )
      } else {
        CreateNewTaskItem { onEvent(ShareEvent.ShowTaskInput) }
      }
    }

    HorizontalPager(
      state = pagerState,
      modifier = Modifier.fillMaxSize()
        .graphicsLayer {
          alpha = if (taskInputActive) 0.5f else 1f
        },
      key = { model.scopes[it].id }
    ) { page ->

      val scope = model.scopes[page]

      val scopeViewModel = koinInjectOnRoute(
        type = ScopeTasksViewModel::class,
        key = "Scope${scope.id}",
        parameters = { parametersOf(scope.id) }
      )

      val scopeTasks by scopeViewModel.models.collectAsState()

      when (val tasksModel = scopeTasks) {
        is ScopeTasksModels.Data -> {
          if (tasksModel.tasks.isEmpty()) {
            EmptyScopeContent { onEvent(ShareEvent.ShowTaskInput) }
          } else {
            ScopeContent(
              tasks = tasksModel.tasks,
              onItemClick = { taskId -> shareToTask(taskId) },
              listState = states[page],
              enabled = !taskInputActive,
              onReorder = {},
              isReordering = false,
              onToggleReordering = {},

            )
          }
        }

        ScopeTasksModels.Loading -> {

        }
      }
    }
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

          HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 0.8.dp
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

        HorizontalDivider(
          modifier = Modifier.fillMaxWidth(),
          thickness = 0.8.dp
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
      text = stringResource(Res.string.create_new_chat),
      textAlign = TextAlign.Start,
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
    )
  }
}

//@Preview
//@Composable
//fun ShareContentPreview() {
//    MoreStuffTheme() {
//        ShareContent(
//            shareable = Shareable.Text(""),
//            tasks = listOf(),
//            shareToTask = {},
//        )
//    }
//}
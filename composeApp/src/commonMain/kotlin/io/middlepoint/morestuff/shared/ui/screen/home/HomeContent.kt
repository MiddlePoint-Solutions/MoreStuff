package io.middlepoint.morestuff.shared.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.backhandler.BackCallback
import io.github.xxfast.decompose.router.LocalRouterContext
import io.middlepoint.morestuff.shared.domain.nav.Screen
import io.middlepoint.morestuff.shared.domain.service.logger
import io.middlepoint.morestuff.shared.ui.components.ConfirmDeleteDialog
import io.middlepoint.morestuff.shared.ui.components.EmptyScopeContent
import io.middlepoint.morestuff.shared.ui.components.HomeTopBar
import io.middlepoint.morestuff.shared.ui.components.InputItem
import io.middlepoint.morestuff.shared.ui.components.MoreStuffHomeScaffold
import io.middlepoint.morestuff.shared.ui.extension.checkRegister
import io.middlepoint.morestuff.shared.ui.extension.checkUnregister
import io.middlepoint.morestuff.shared.ui.local.LocalAppRouter
import io.middlepoint.morestuff.shared.ui.model.show
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.CompleteSelectedTasks
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.CreateScope
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.CreateScopeForSelectedTasks
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.CreateTask
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.DeleteSelectedTasks
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.MoveSelectedTasksToScope
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.ResetHomeState
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.ScopeSelected
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.ShowTaskInput
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.ToggleScopeReordering
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.ToggleTaskSelection
import io.middlepoint.morestuff.shared.ui.screen.schedule.ScopeContent
import io.middlepoint.morestuff.shared.ui.screen.schedule.ScopeTasksEvent
import io.middlepoint.morestuff.shared.ui.screen.schedule.ScopeTasksModels
import io.middlepoint.morestuff.shared.ui.screen.schedule.ScopeTasksViewModel
import io.middlepoint.morestuff.shared.ui.screen.search.SearchBar
import io.middlepoint.morestuff.shared.ui.screen.settings.koinInjectOnRoute
import io.middlepoint.morestuff.shared.ui.theme.surfaceContainerElevation
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

  val homePresenter = koinInjectOnRoute(HomePresenter::class)

  val coroutineScope = rememberCoroutineScope()
  val navigation = LocalAppRouter.current
  val snackbarHostState = remember { SnackbarHostState() }
  var isSearchActive by rememberSaveable { mutableStateOf(false) }
  var showScopeSelection by remember { mutableStateOf(false) }
  var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

  val model by homePresenter.models.collectAsState()
  val pendingCompletionTasks = remember { mutableStateMapOf<Long, Job>() }
  var isReorderingActive = remember { mutableStateOf(false) }


  MoreStuffHomeScaffold(
    snackbarHostState = snackbarHostState,
    topBar = {
      HomeTopBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainerElevation,
        selectedTaskCount = model.selectedTasks.size,
        reviewSelected = { navigation.push(Screen.Review(model.currentScopeId)) },
        settingsSelected = { navigation.push(Screen.Settings) },
        searchAction = { isSearchActive = true },
        clearTaskSelection = {
          homePresenter.take(ResetHomeState)
          isReorderingActive.value = false
        },
        completeSelectedTasks = { homePresenter.take(CompleteSelectedTasks) },
        deleteSelectedTasks = { showDeleteConfirmationDialog = true },
        selectScope = { showScopeSelection = true },
        isReorderingActive = isReorderingActive.value
      )
    },
    content = {
      if (model.scopes.isNotEmpty()) {
        HomeContent(
          model = model,
          onEvent = homePresenter::take,
          modifier = Modifier.padding(it),
          createNewScope = {
            val createScopeScreen = Screen.CreateScope {
              homePresenter.take(CreateScope(it))
              navigation.pop()
            }
            navigation.push(createScopeScreen)
          },
          onTaskComplete = { taskId ->
            if (pendingCompletionTasks.contains(taskId)) {
              pendingCompletionTasks[taskId]?.cancel()
              pendingCompletionTasks.remove(taskId)
            } else {
              val job = coroutineScope.launch {
                delay(1000)
                homePresenter.take(HomeEvent.CompleteTask(taskId))
                pendingCompletionTasks.remove(taskId)
              }
              pendingCompletionTasks[taskId] = job
            }
          },
          onReorderingChanged = { isReordering -> isReorderingActive.value = isReordering }
        )
      }
    },
  )

  LaunchedEffect(Unit) {
    homePresenter.notifications.collectLatest { notification ->
      notification.show(snackbarHostState).let { result ->
        if (result == SnackbarResult.ActionPerformed) {
          launch { notification.action() }
        }
      }
    }
  }

  if (showDeleteConfirmationDialog) {
    ConfirmDeleteDialog(
      onDismiss = { showDeleteConfirmationDialog = false },
      onConfirm = {
        homePresenter.take(DeleteSelectedTasks)
        showDeleteConfirmationDialog = false
      }
    )
  }

  AnimatedVisibility(
    visible = isSearchActive,
    enter = fadeIn(),
    exit = fadeOut(),
  ) {
    SearchBar(
      onSearchClose = { isSearchActive = false },
      showTaskChat = { navigation.push(Screen.TaskChat(it)) },
      modifier = Modifier.fillMaxWidth()
    )
  }

  if (showScopeSelection) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ScopeSelectionBottomSheet(
      onDismissRequest = {
        coroutineScope.launch {
          sheetState.hide()
          showScopeSelection = false
        }
      },
      scopes = model.scopes,
      sheetState = sheetState,
      addSelectedTasksToScope = { homePresenter.take(MoveSelectedTasksToScope(it)) },
      createNewScope = {
        val createScopeScreen = Screen.CreateScope {
          homePresenter.take(CreateScopeForSelectedTasks(it))
          navigation.pop()
        }
        navigation.push(createScopeScreen)
      }
    )
  }
}

@Composable
private fun HomeContent(
  model: HomeState,
  onEvent: (HomeEvent) -> Unit,
  createNewScope: () -> Unit,
  onTaskComplete: (Long) -> Unit,
  modifier: Modifier = Modifier,
  onReorderingChanged: (Boolean) -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  val navigation = LocalAppRouter.current

  val selectedTasks = model.selectedTasks
  val taskInputActive = model.taskInputActive

  val states by rememberUpdatedState(newValue = model.scopes.map { rememberLazyListState() })
  val pagerState = rememberPagerState(pageCount = { model.scopes.size })
  var currentScopePage by remember { mutableIntStateOf(pagerState.currentPage) }
  val scopes by rememberUpdatedState(newValue = model.scopes)
  val previousScopesSize = remember { mutableStateOf(model.scopes.size) }
  val schedule = model.planTime


  LaunchedEffect(Unit) {
    snapshotFlow { pagerState.currentPage }
      .collect { page ->
        if (currentScopePage != page) {
          launch { states[currentScopePage].animateScrollToItem(0) }
          currentScopePage = page
        }
        onEvent(ScopeSelected(scopes[page].id))
      }
  }

  LaunchedEffect(model.scopes.size) {
    val newSize = model.scopes.size
    if (newSize > previousScopesSize.value) {
      pagerState.animateScrollToPage(newSize - 1)
    }
    previousScopesSize.value = newSize
  }


  val backCallback = remember {
    BackCallback {
      onEvent(ResetHomeState)
    }
  }

  val backHandler = LocalRouterContext.current.backHandler
  LaunchedEffect(model) {
    if (selectedTasks.isNotEmpty() || taskInputActive) {
      backHandler.checkRegister(backCallback)
    } else {
      backHandler.checkUnregister(backCallback)
    }
  }

  Box(
    modifier = modifier.fillMaxSize()
  ) {
    Column {
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
          createNewScope = createNewScope
        )
      }

      AnimatedVisibility(
        visible = taskInputActive,
        enter = expandVertically(),
        exit = shrinkVertically(),
      ) {
        InputItem(
          onDone = { text ->
            coroutineScope.launch {
              if (model.planTime != null) {
                onEvent(HomeEvent.CreateTaskWithSchedule(text))
              } else {
                onEvent(CreateTask(text))
              }
              delay(200)
              states[pagerState.currentPage].animateScrollToItem(index = 0)
            }
          },
          onCancel = { onEvent(ResetHomeState) },
          onDateChange = { onEvent(HomeEvent.UpdatePlanDate(it)) },
          onTimeChange = { h, m -> onEvent(HomeEvent.UpdatePlanTime(h, m)) },
          onSetPriority = { onEvent(HomeEvent.SetPlanPriority) },
          onClearSetPriority = { onEvent(HomeEvent.ClearPlanPriority) },
          schedule = schedule,
        )
      }

      HorizontalPager(

        state = pagerState,
        modifier = Modifier.fillMaxSize()
          .graphicsLayer {
            alpha = if (taskInputActive) 0.4f else 1f
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
              EmptyScopeContent { onEvent(ShowTaskInput) }
            } else {
              ScopeContent(
                tasks = tasksModel.tasks,
                selectedTasks = selectedTasks,
                onItemClick = { taskId ->
                  if (selectedTasks.isNotEmpty()) {
                    onEvent(ToggleTaskSelection(taskId))
                  } else {
                    navigation.push(Screen.TaskChat(taskId))
                  }
                },
                onItemLongClick = { onEvent(ToggleTaskSelection(it)) },
                listState = states[page],
                enabled = !taskInputActive,
                onReorder = { updatedTasks ->
                  logger.d { "Reordering tasks..." }
                  scopeViewModel.take(ScopeTasksEvent.ReorderTasks(updatedTasks))
                },
                isReordering = model.reorderingScopes[scope.id] ?: false,
                onToggleReordering = { newValue ->
                  model.scopes.forEach { scope ->
                    onEvent(ToggleScopeReordering(scope.id, newValue))
                  }

                  onReorderingChanged(newValue)

                },
                onTaskComplete = { taskId ->
                  onTaskComplete(taskId)
                },
                onClearSelection = { onEvent(ResetHomeState) }
              )
            }
          }

          ScopeTasksModels.Loading -> {
          }
        }
      }
    }

    AnimatedVisibility(
      visible = !taskInputActive,
      modifier = Modifier
        .padding(20.dp)
        .align(Alignment.BottomEnd),
      enter = scaleIn() + fadeIn(),
      exit = scaleOut() + fadeOut()
    ) {
      FloatingActionButton(
        onClick = { onEvent(ShowTaskInput) },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
      ) {
        Icon(Icons.Default.Add, contentDescription = null)
      }
    }

  }
}

//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    name = "Dark"
//)
//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_NO,
//    name = "Light"
//)
//@Composable
//fun MainContentPreview() {
//    MoreStuffTheme {
//        HomeScreen()
//    }
//}

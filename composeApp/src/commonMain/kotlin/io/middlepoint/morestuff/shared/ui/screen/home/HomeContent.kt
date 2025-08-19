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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.ui.components.CreateScopeBottomSheet
import io.middlepoint.morestuff.shared.ui.components.DeleteBottomSheet
import io.middlepoint.morestuff.shared.ui.components.EmptyScopeContent
import io.middlepoint.morestuff.shared.ui.components.HomeTopBar
import io.middlepoint.morestuff.shared.ui.components.InputItem
import io.middlepoint.morestuff.shared.ui.components.MoreStuffHomeScaffold
import io.middlepoint.morestuff.shared.ui.model.NotificationState
import io.middlepoint.morestuff.shared.ui.model.show
import io.middlepoint.morestuff.shared.ui.platform.BackHandler
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.ClearPlanPriority
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.CompleteSelectedTasks
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.CompleteTask
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.CreateScope
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.CreateTask
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.CreateTaskWithSchedule
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.DeleteSelectedTasks
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.HideTaskInput
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.MoveSelectedTasksToScope
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.ResetHomeState
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.ScopeSelected
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.SetPlanPriority
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.ShowTaskInput
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.ToggleScopeReordering
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.ToggleTaskSelection
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.UpdatePlanDate
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.UpdatePlanTime
import io.middlepoint.morestuff.shared.ui.screen.schedule.ScopeContent
import io.middlepoint.morestuff.shared.ui.screen.schedule.ScopeTasksEvent
import io.middlepoint.morestuff.shared.ui.screen.schedule.ScopeTasksModels
import io.middlepoint.morestuff.shared.ui.screen.schedule.ScopeTasksViewModel
import io.middlepoint.morestuff.shared.ui.screen.search.SearchBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.cancel
import morestuff.composeapp.generated.resources.confirm_delete
import morestuff.composeapp.generated.resources.delete
import morestuff.composeapp.generated.resources.task_schedule_deletion_warning_plural
import morestuff.composeapp.generated.resources.task_schedule_deletion_warning_singular
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun HomeScreen(
  navigateToSettings: () -> Unit,
  navigateToTaskChat: (Uuid) -> Unit,
) {

  val homeState = koinViewModel<HomeViewModel>()

  val coroutineScope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }
  var isSearchActive by rememberSaveable { mutableStateOf(false) }

  var showScopeSelection by remember { mutableStateOf(false) }
  val showScopeSelectionSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val deleteSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var showDeleteBottomSheet by remember { mutableStateOf(false) }

  var showCreateScopeSheet by remember { mutableStateOf(false) }
  val createScopeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val model by homeState.models.collectAsState()
  var isReorderingActive by remember { mutableStateOf(false) }

  BackHandler(isReorderingActive) {
    homeState.take(ResetHomeState)
    isReorderingActive = false
  }

  MoreStuffHomeScaffold(
    snackbarHostState = snackbarHostState,
    topBar = {
      HomeTopBar(
        username = model.username,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        selectedTaskCount = model.selectedTasks.size,
        settingsSelected = navigateToSettings,
        searchAction = { isSearchActive = true },
        clearTaskSelection = {
          homeState.take(ResetHomeState)
          isReorderingActive = false
        },
        completeSelectedTasks = { homeState.take(CompleteSelectedTasks) },
        deleteSelectedTasks = { showDeleteBottomSheet = true },
        selectScope = { showScopeSelection = true },
        isReorderingActive = isReorderingActive
      )
    },
    content = {
      if (model.scopes.isNotEmpty()) {
        HomeContent(
          model = model,
          onEvent = homeState::take,
          modifier = Modifier.padding(it),
          createNewScope = {
            showCreateScopeSheet = true
          },
          onTaskComplete = { taskId ->
            homeState.take(CompleteTask(taskId))
          },
          onReorderingChanged = { isReordering -> isReorderingActive = isReordering },
          navigateToTaskChat = navigateToTaskChat
        )
      }
    },
  )

  LaunchedEffect(Unit) {
    homeState.notifications.collectLatest { notification ->
      notification.show(snackbarHostState).let { result ->
        if (result == SnackbarResult.ActionPerformed) {
          launch {
            notification.action()
            if (notification is NotificationState.Complete) {
              homeState.take(ResetHomeState)
            }
          }
        }
      }
    }
  }

  if (showDeleteBottomSheet) {
    val taskCount = model.selectedTasks.size
    val message = if (taskCount == 1) {
      val taskId = model.selectedTasks.first()
      val taskName = model.tasks[taskId]?.title ?: ""
      taskName
    } else {
      model.selectedTasks.joinToString(separator = ", ") { taskId ->
        model.tasks[taskId]?.title ?: ""
      }
    }
    val hasScheduledTask = model.selectedTasks.any { taskId ->
      model.tasks[taskId]?.hasSchedule == true
    }

    val taskHasSchedule = if (hasScheduledTask) {
      if (taskCount == 1) {
        stringResource(Res.string.task_schedule_deletion_warning_singular)
      } else {
        stringResource(Res.string.task_schedule_deletion_warning_plural)
      }
    } else {
      null
    }

    DeleteBottomSheet(
      sheetState = deleteSheetState,
      onDismissRequest = { showDeleteBottomSheet = false },
      title = stringResource(Res.string.confirm_delete),
      message = message,
      confirmButtonText = stringResource(Res.string.delete),
      dismissButtonText = stringResource(Res.string.cancel),
      onConfirm = {
        homeState.take(DeleteSelectedTasks)
        showDeleteBottomSheet = false
      },
      extraInfo = taskHasSchedule
    )
  }

  AnimatedVisibility(
    visible = isSearchActive,
    enter = fadeIn(),
    exit = fadeOut(),
  ) {
    SearchBar(
      onSearchClose = { isSearchActive = false },
      showTaskChat = navigateToTaskChat,
      modifier = Modifier.fillMaxWidth()
    )
  }

  if (showScopeSelection) {
    ScopeSelectionBottomSheet(
      onDismissRequest = {
        coroutineScope.launch {
          showScopeSelectionSheetState.hide()
          showScopeSelection = false
        }
      },
      scopes = model.scopes,
      sheetState = showScopeSelectionSheetState,
      addSelectedTasksToScope = { homeState.take(MoveSelectedTasksToScope(it)) },
      createNewScope = {
        showCreateScopeSheet = true
      }
    )
  }

  if (showCreateScopeSheet) {
    CreateScopeBottomSheet(
      sheetState = createScopeSheetState,
      onDismissRequest = {
        coroutineScope.launch {
          createScopeSheetState.hide()
          showCreateScopeSheet = false
        }
      },
      onConfirm = { scopeName ->
        if (model.selectedTasks.isNotEmpty()) {
          homeState.take(HomeEvent.CreateScopeForSelectedTasks(scopeName))
        } else {
          homeState.take(CreateScope(scopeName))
        }
        coroutineScope.launch {
          createScopeSheetState.hide()
          showCreateScopeSheet = false
        }
      }
    )
  }
}

@Composable
private fun HomeContent(
  model: HomeState,
  onEvent: (HomeEvent) -> Unit,
  createNewScope: () -> Unit,
  onTaskComplete: (Uuid) -> Unit,
  modifier: Modifier = Modifier,
  onReorderingChanged: (Boolean) -> Unit,
  navigateToTaskChat: (Uuid) -> Unit
) {
  val coroutineScope = rememberCoroutineScope()

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
          containerColor = MaterialTheme.colorScheme.surfaceContainer,
          createNewScope = createNewScope,
          isCreateScopeVisible = model.selectedTasks.isEmpty()
        )
      }

      if (model.syncInProgress) {
        LinearProgressIndicator(
          modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
        )
      }

      AnimatedVisibility(
        visible = taskInputActive,
        enter = expandVertically(),
        exit = shrinkVertically(),
      ) {

        BackHandler {
          onEvent(ResetHomeState)
          onEvent(HideTaskInput)
        }

        InputItem(
          onDone = { text ->
            coroutineScope.launch {
              if (model.planTime != null) {
                onEvent(CreateTaskWithSchedule(text))
              } else {
                onEvent(CreateTask(text))
              }
              delay(200)
              states[pagerState.currentPage].animateScrollToItem(index = 0)
            }
          },
          onCancel = {
            onEvent(ResetHomeState)
            onEvent(HideTaskInput)
          },
          onDateChange = { onEvent(UpdatePlanDate(it)) },
          onTimeChange = { h, m -> onEvent(UpdatePlanTime(h, m)) },
          onSetPriority = { onEvent(SetPlanPriority) },
          onClearSetPriority = { onEvent(ClearPlanPriority) },
          schedule = schedule,
        )
      }

      HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
          .graphicsLayer {
            alpha = if (taskInputActive) 0.4f else 1f
          },
        key = { model.scopes[it].id.value }
      ) { page ->

        val scope = model.scopes[page]

        val scopeViewModel = koinViewModel<ScopeTasksViewModel>(
          key = "Scope${scope.id.value}",
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
                  if (taskInputActive) {
                    //onEvent(HomeEvent.HideTaskInput)
                  } else if (selectedTasks.isNotEmpty()) {
                    val task = tasksModel.tasks.find { it.id == taskId }
                    onEvent(ToggleTaskSelection(taskId, task))
                  } else {
                    navigateToTaskChat(taskId)
                  }
                },
                onItemLongClick = { taskId ->
                  val task = tasksModel.tasks.find { it.id == taskId }
                  onEvent(ToggleTaskSelection(taskId, task))
                },
                listState = states[page],
                enabled = !taskInputActive,
                onReorder = { updatedTasks ->
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
                onClearSelection = {
                  onEvent(ResetHomeState)
                }
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

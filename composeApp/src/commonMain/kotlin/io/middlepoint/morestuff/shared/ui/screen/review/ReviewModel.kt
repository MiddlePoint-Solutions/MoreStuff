package io.middlepoint.morestuff.shared.ui.screen.review

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.middlepoint.morestuff.shared.domain.enums.ReviewActionType
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.action.PriorityAction
import io.middlepoint.morestuff.shared.domain.redux.action.TaskAction
import io.middlepoint.morestuff.shared.domain.redux.action.SettingAction
import io.middlepoint.morestuff.shared.domain.usecase.scope.GetScopesUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.GetReviewTasksUseCase
import io.middlepoint.morestuff.shared.ui.model.map.ReviewTasksMapper
import io.middlepoint.morestuff.shared.ui.components.swipeable.SwipeDirection
import io.middlepoint.morestuff.shared.ui.model.ReviewItemUiModel
import io.middlepoint.morestuff.shared.ui.screen.review.ReviewViewEvent.*
import kotlinx.coroutines.flow.Flow
import org.koin.compose.koinInject


@Composable
fun reviewModel(
  initialState: ReviewState,
  events: Flow<ReviewViewEvent>,
  store: AppStore = koinInject(),
  getReviewTasksUseCase: GetReviewTasksUseCase = koinInject(),
  getScopesUseCase: GetScopesUseCase = koinInject(),
  reviewTasksMapper: ReviewTasksMapper = koinInject()
): ReviewState {

  var round by remember { mutableStateOf(initialState.round) }
  var currentScope by remember { mutableStateOf(initialState.currentScope) }
  var items: List<ReviewItemUiModel> by remember { mutableStateOf(initialState.items) }
  var actions by remember { mutableStateOf(initialState.actions) }
  var scopes by remember { mutableStateOf(initialState.scopes) }
  var reviewHintEnabled by remember { mutableStateOf(initialState.reviewHintEnabled) }
  var itemsForReview by remember { mutableIntStateOf(0) }

  LaunchedEffect(Unit) {
    getScopesUseCase().onRight { scopes = it }
    reviewHintEnabled = store.currentState.settings.enableReviewHint
  }

  fun checkUpdateRound(item: ReviewItemUiModel) {
    if (items.indexOf(item) == 0) {
      round = ReviewRound.Final
    } else {
      itemsForReview -= 1
    }
  }

  LaunchedEffect(Unit) {
    events.collect { event ->
      when (event) {
        is ItemSwipe -> {
          val actionType = when (event.direction) {
            SwipeDirection.Left -> ReviewActionType.Less
            SwipeDirection.Right -> ReviewActionType.More
            SwipeDirection.Up -> ReviewActionType.Now
            SwipeDirection.Down -> ReviewActionType.Later
            SwipeDirection.None -> null
          }
          actionType?.let {
            store.dispatch(PriorityAction.TaskPriorityUpdateAction(event.item.id, it))
            actions = actions + (event.item to it)
            checkUpdateRound(event.item)
          }
        }

        is CompleteTask -> {
          store.dispatch(TaskAction.CompleteTasksAction(listOf(event.item.id), true))
          actions = actions + (event.item to ReviewActionType.Done)
          checkUpdateRound(event.item)
        }

        is DeleteTask -> {
          store.dispatch(TaskAction.DeleteTasksAction(listOf(event.item.id)))
          items = items - event.item
          checkUpdateRound(event.item)
        }

        is Undo -> {
          actions.firstOrNull { it.first.id == event.item.id }?.let {
            when (it.second) {
              ReviewActionType.Done -> store.dispatch(
                TaskAction.CompleteTasksAction(
                  listOf(event.item.id),
                  false
                )
              )

              else -> store.dispatch(
                PriorityAction.UndoTaskPriorityUpdateAction(
                  event.item.id,
                  event.item.priorityScore
                )
              )
            }
            itemsForReview += 1
          }
          actions = actions.filterNot { it.first.id == event.item.id }
        }

        is ToggleReviewHint -> {
          reviewHintEnabled = !reviewHintEnabled
          store.dispatch(SettingAction.EnableReviewHint(reviewHintEnabled))
        }

        is LoadScope -> {
          getReviewTasksUseCase(event.scopeId).map {
            items = reviewTasksMapper.map(it.tasks, it.taskMessages)
          }
          currentScope = scopes.firstOrNull { it.id == event.scopeId } ?: currentScope
          round = ReviewRound.Review(event.scopeId)
          itemsForReview = items.size
        }
      }
    }
  }

  return ReviewState(
    round,
    currentScope,
    items,
    itemsForReview,
    actions,
    scopes,
    reviewHintEnabled
  )
}


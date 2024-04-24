package co.softov.morestuff.android.ui.review

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import arrow.core.Either
import co.softov.morestuff.android.domain.enums.PriorityActionType
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.PriorityAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.redux.state.SettingAction
import co.softov.morestuff.android.domain.usecase.scope.GetScopesUseCase
import co.softov.morestuff.android.domain.usecase.task.GetReviewTasksUseCase
import co.softov.morestuff.android.ui.model.map.ReviewTasksMapper
import co.softov.morestuff.android.ui.review.swipeable.SwipeDirection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
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
    var items by remember { mutableStateOf(initialState.items) }
    var actions by remember { mutableStateOf(initialState.actions) }
    var scopes by remember { mutableStateOf(initialState.scopes) }
    var reviewHintEnabled by remember { mutableStateOf(initialState.reviewHintEnabled) }

    LaunchedEffect(currentScope) {
        launch {
            getScopesUseCase().onRight {
                scopes = it
                currentScope.id
            }
        }
    }


    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is ReviewViewEvent.ItemSwipe -> {
                    val actionType = when (event.direction) {
                        SwipeDirection.Left -> PriorityActionType.Less
                        SwipeDirection.Right -> PriorityActionType.More
                        SwipeDirection.Up -> PriorityActionType.Now
                        SwipeDirection.Down -> PriorityActionType.Later
                        else -> null
                    }
                    actionType?.let {
                        store.dispatch(PriorityAction.TaskPriorityUpdateAction(event.item.id, it))
                        actions = actions + (event.item to it)
                        if (items.indexOf(event.item) == 0) {
                            round = ReviewRound.Final
                        }
                    }
                }

                is ReviewViewEvent.CompleteTask -> {
                    store.dispatch(TaskAction.CompleteTasksAction(listOf(event.item.id), true))
                    actions = actions + (event.item to PriorityActionType.Done)
                    if (items.indexOf(event.item) == 0) {
                        round = ReviewRound.Final
                    }
                }

                is ReviewViewEvent.Undo -> {
                    val action = actions.firstOrNull { it.first.id == event.item.id }
                    action?.let {
                        when (it.second) {
                            PriorityActionType.Done -> store.dispatch(
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
                    }
                    actions = actions.filterNot { it.first.id == event.item.id }
                }

               is ReviewViewEvent.ToggleReviewHint -> {
                    reviewHintEnabled = !reviewHintEnabled
                    store.dispatch(SettingAction.EnableReviewHint(reviewHintEnabled))
                }

                is ReviewViewEvent.LoadScope -> {
                    launch {
                        val scopesResult = getScopesUseCase()
                        if (scopesResult is Either.Right) {
                            scopes = scopesResult.value
                            val scope = scopes.firstOrNull { it.id == event.scopeId }
                            if (scope != null && (currentScope.id != scope.id || items.isEmpty())) {
                                val taskResult = getReviewTasksUseCase(scope.id)
                                if (taskResult is Either.Right) {
                                    val tasks = taskResult.value
                                    val mappedTasks = tasks.tasks.mapIndexed { index, task ->
                                        reviewTasksMapper.internalMap(
                                            task,
                                            index + 1,
                                            tasks.tasks.size
                                        )
                                    }.shuffled()
                                    items = mappedTasks
                                    currentScope = scope
                                    round = ReviewRound.Review(scope.id)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    return ReviewState(round, currentScope, items, actions, scopes, reviewHintEnabled)
}


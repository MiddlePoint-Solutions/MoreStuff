package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.redux.*
import co.softov.morestuff.android.domain.redux.state.PriorityAction
import co.softov.morestuff.android.domain.usecase.priority.GetPriorityOptionsParams
import co.softov.morestuff.android.domain.usecase.priority.GetPriorityOptionsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PriorityMiddleware(
    private val getPriorityOptionsUseCase: GetPriorityOptionsUseCase,
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        when (action) {
            is Init -> getPriorityOptions(scope, state.currentPriority, dispatch)
            is PriorityAction.SetPriority -> getPriorityOptions(scope, action.priority, dispatch)
            else -> NoOp
        }
        return next(state, action, dispatch)
    }

    private fun getPriorityOptions(
        scope: CoroutineScope,
        priority: Priority,
        dispatch: Dispatch
    ) {
        scope.launch {
            val params = GetPriorityOptionsParams(priority)
            getPriorityOptionsUseCase(params).fold(
                ifLeft = {
                    dispatch(ErrorAction(it))
                },
                ifRight = {
                    dispatch(
                        PriorityAction.SetPriorityOptions(
                            priority = it.priority,
                            options = it.options
                        )
                    )
                }
            )
        }
    }
}
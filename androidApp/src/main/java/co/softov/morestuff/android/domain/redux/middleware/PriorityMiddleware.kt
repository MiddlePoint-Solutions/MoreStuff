package co.softov.morestuff.android.domain.redux.middleware

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
            is PriorityAction.SetPriority -> getPriorityOptions(scope, action, dispatch)
            else -> NoOp
        }
        return next(state, action, dispatch)
    }

    private fun getPriorityOptions(
        scope: CoroutineScope,
        action: PriorityAction.SetPriority,
        dispatch: Dispatch
    ) {
        scope.launch {
            val params = GetPriorityOptionsParams(action.priority)
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
package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.Dispatch
import co.softov.morestuff.android.domain.redux.store.Next
import kotlinx.coroutines.CoroutineScope

interface Middleware<State> {
    operator fun invoke(
        state: State,
        action: Action,
        dispatch: Dispatch,
        next: Next<State>,
        scope: CoroutineScope
    ): Action
}
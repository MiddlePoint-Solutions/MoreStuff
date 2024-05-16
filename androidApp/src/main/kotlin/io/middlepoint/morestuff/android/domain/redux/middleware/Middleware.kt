package io.middlepoint.morestuff.android.domain.redux.middleware

import io.middlepoint.morestuff.android.domain.redux.store.Action
import io.middlepoint.morestuff.android.domain.redux.store.Dispatch
import io.middlepoint.morestuff.android.domain.redux.store.Next
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
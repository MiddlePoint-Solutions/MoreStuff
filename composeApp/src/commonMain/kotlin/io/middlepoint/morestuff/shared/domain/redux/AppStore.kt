package io.middlepoint.morestuff.shared.domain.redux

import io.middlepoint.morestuff.shared.domain.redux.state.AppState
import io.middlepoint.morestuff.shared.domain.redux.state.reduceSettingState
import io.middlepoint.morestuff.shared.domain.redux.state.reduceUserState
import io.middlepoint.morestuff.shared.domain.redux.store.SimpleStore

class AppStore(
  provider: MiddlewareProvider<AppState>
) : SimpleStore<AppState>(
  startingState = AppState(),
  reducers = listOf(
    AppState::reduceUserState,
    AppState::reduceSettingState,
  ),
  middleware = provider.middlewareOrder
)


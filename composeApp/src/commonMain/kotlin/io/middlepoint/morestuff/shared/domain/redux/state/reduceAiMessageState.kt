package io.middlepoint.morestuff.shared.domain.redux.state

import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.action.MessageAction
import io.middlepoint.morestuff.shared.domain.model.Uuid


data class AiMessageState(
  val loadingMap: Map<Uuid, Boolean> = emptyMap(),
)



fun AppState.reduceAiMessageState(action: Action): AppState = when (action) {

  is MessageAction.AIRequestStarted -> copy(
    aiMessageState = aiMessageState.copy(
      loadingMap = aiMessageState.loadingMap + (action.taskId to true)
    )
  )

  is MessageAction.AIRequestFinished -> copy(
    aiMessageState = aiMessageState.copy(
      loadingMap = aiMessageState.loadingMap + (action.taskId to false)
    )
  )

  is MessageAction.AIRequestFailed -> copy(
    aiMessageState = aiMessageState.copy(
      loadingMap = aiMessageState.loadingMap + (action.taskId to false)
    )
  )

  else -> this
}

package io.middlepoint.morestuff.shared.domain.redux.state

import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.action.MessageAction


fun AppState.reduceAiMessageState(action: Action): AppState = when (action) {

  is MessageAction.AIRequestStarted ->
    copy(
      aiMessageLoading = aiMessageLoading + (action.taskId to true),
      aiMessageErrors = aiMessageErrors - action.taskId
    )

  is MessageAction.AIRequestFinished ->
    copy(
      aiMessageLoading = aiMessageLoading + (action.taskId to false),
      aiMessageErrors = aiMessageErrors - action.taskId
    )

  is MessageAction.AIRequestFailed ->
    copy(
      aiMessageLoading = aiMessageLoading + (action.taskId to false),
      aiMessageErrors = aiMessageErrors + (action.taskId to (action.error ?: "Unknown error"))
    )

  is MessageAction.ClearAIErrorAction -> copy(
    aiMessageErrors = aiMessageErrors - action.taskId
  )

  else -> this
}
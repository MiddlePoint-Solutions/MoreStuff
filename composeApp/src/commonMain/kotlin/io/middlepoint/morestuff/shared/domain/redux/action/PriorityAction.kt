package io.middlepoint.morestuff.shared.domain.redux.action

import io.middlepoint.morestuff.shared.domain.enums.ReviewActionType
import io.middlepoint.morestuff.shared.domain.redux.store.Action

sealed class PriorityAction : Action.FeatureAction() {

    data class UndoTaskPriorityUpdateAction(
        val taskId: Long,
        val score: Long,
    ) : PriorityAction()

    data class TaskPriorityUpdateAction(
      val task: Long,
      val actionType: ReviewActionType,
    ) : PriorityAction()

    data object UpdatePlannedPriorityAction : PriorityAction()

}
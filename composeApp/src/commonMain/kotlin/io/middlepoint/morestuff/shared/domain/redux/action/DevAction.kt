package io.middlepoint.morestuff.shared.domain.redux.action

import io.middlepoint.morestuff.shared.domain.redux.store.Action

sealed class DevAction : Action.FeatureAction() {
    data object ClearActiveReminderMessages : DevAction()
}
package io.middlepoint.morestuff.shared.ui.screen.home

import io.middlepoint.morestuff.shared.ui.model.NotificationState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class AppPresenter {

    val notifications = MutableSharedFlow<NotificationState>()

}
package io.middlepoint.morestuff.android.ui.home

import io.middlepoint.morestuff.android.ui.model.NotificationState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class AppPresenter {

    val notifications = MutableSharedFlow<NotificationState>()

}
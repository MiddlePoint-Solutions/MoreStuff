package co.softov.morestuff.android.ui.home

import co.softov.morestuff.android.ui.model.NotificationState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class AppPresenter {

    val notifications = MutableSharedFlow<NotificationState>(1)

}
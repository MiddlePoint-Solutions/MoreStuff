package io.middlepoint.morestuff.shared.domain.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.nav.Screen
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.action.ScheduleAction
import io.middlepoint.morestuff.shared.domain.usecase.schedule.CancelActiveScheduleUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


val logger = Logger.withTag("NavigationHelper")

class NavigationHelper: ViewModel(), KoinComponent {

    val shareable = MutableSharedFlow<Shareable>()
    val navigation = MutableSharedFlow<Screen>()
    val code = MutableSharedFlow<String>()
    private val cancelActiveScheduleUseCase: CancelActiveScheduleUseCase by inject()
    private val scheduler: Scheduler by inject()
    private val store: AppStore by inject()


    fun shareText(message: String) {
        viewModelScope.launch {
            logger.d { "Emitting Shareable.Text with message: $message" }
            shareable.emit(Shareable.Text(message))
        }
    }

    fun shareImage(uri: String) {
        viewModelScope.launch {
            logger.d { "Emitting Shareable.Image with uri: $uri," }
            shareable.emit(Shareable.Image(uri, ""))
        }
    }

    fun sharePdf(uri: String) {
        viewModelScope.launch {
            logger.d { "Emitting Shareable.Pdf with uri: $uri" }
            shareable.emit(Shareable.Pdf(uri, ""))
        }
    }

    fun navigateToTaskChat(taskId: Uuid) {
        viewModelScope.launch {
            logger.d { "Emitting Screen to navigate to: $taskId" }
            navigation.emit(Screen.TaskChat(taskId))
          cancelActiveScheduleUseCase(listOf(taskId))
        }
    }

    fun cancelTaskSchedule(taskId: Uuid) {
        viewModelScope.launch {
            logger.d { "Cancelling active schedule for taskId: $taskId" }

            val result = cancelActiveScheduleUseCase.invoke(listOf(taskId))

            result.onRight { schedules ->
                logger.d { "Cancelled schedules: ${schedules.map { it.id }}" }
            }.onLeft { failure ->
                logger.e { "Failed to cancel schedule: $failure" }
            }
        }
    }

    fun navigateToHome(accessToken: String? = null) {
        logger.d { "este es el accessToken: $accessToken" }
        viewModelScope.launch {
            if (accessToken != null) {
                logger.d { "Emitting Screen.Home with access token" }
                code.emit(accessToken)
                navigation.emit(Screen.Home)
            } else {
                logger.d { "Emitting Screen.Home without token" }
                navigation.emit(Screen.Home)
            }
        }
    }

    fun triggerDataSyncSchedule() {
        logger.d { "Calling scheduleDataSyncWorker from NavigationHelper" }
        scheduler.dataSyncWorker()
    }


    fun replyToSchedule(scheduleId: Uuid, replyTypeString: String) {
        viewModelScope.launch {
            val replyType = ReplyType.valueOf(replyTypeString.uppercase())
            logger.d { "Dispatching ScheduleReplyAction with id: $scheduleId and type: $replyType" }
            store.dispatch(ScheduleAction.ScheduleReplyAction(scheduleId, replyType))
        }
    }



}


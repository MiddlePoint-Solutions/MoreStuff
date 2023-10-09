package co.softov.morestuff.android.ui.home

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.usecase.message.GetMessagesUseCase
import co.softov.morestuff.android.domain.util.TimeFormatter
import co.softov.morestuff.android.ui.chat.task.MessageUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    getMessagesUseCase: GetMessagesUseCase,
    private val timeManager: TimeManager,
    private val timeFormatter: TimeFormatter,
) : NoStateViewModel() {

    val messages: StateFlow<List<MessageUiModel>> = getMessagesUseCase()
        .map { messages ->
            messages.map { message ->
                val messageDateTime = timeManager.utcStringToLocalDateTime(message.createTime)
                val formattedTime =
                    timeFormatter.formatTimeWithDayMonthYear(messageDateTime.toString()) ?: ""
                val formattedTimeOnly =
                    timeFormatter.formatTimeOnly(messageDateTime.toString()) ?: ""
                MessageUiModel(message, formattedTime, formattedTimeOnly)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = listOf()
        )

}
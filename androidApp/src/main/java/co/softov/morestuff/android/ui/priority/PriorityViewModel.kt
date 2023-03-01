package co.softov.morestuff.android.ui.priority

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PriorityOption
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.store.OnResumeAction
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction.UserResponseAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.redux.state.PriorityAction
import co.softov.morestuff.android.domain.usecase.message.GetMessages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PriorityViewModel(
    private val store: AppStore
) : ViewModel(), KoinComponent {

    fun priorityChanged(priority: Priority) {
        store.dispatch(PriorityAction.SetPriority(priority))
    }

    fun onPriorityOptionChanged(option: PriorityOption) {
        store.dispatch(PriorityAction.SetCurrentPriorityOption(option))
    }

}

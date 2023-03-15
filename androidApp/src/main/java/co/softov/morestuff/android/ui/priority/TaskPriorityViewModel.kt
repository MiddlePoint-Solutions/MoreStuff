package co.softov.morestuff.android.ui.priority

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PriorityOption
import co.softov.morestuff.android.domain.model.PriorityOptionsResult
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.state.PriorityAction
import co.softov.morestuff.android.domain.usecase.priority.GetPriorityOptionsParams
import co.softov.morestuff.android.domain.usecase.priority.GetPriorityOptionsUseCase
import co.softov.morestuff.android.domain.usecase.priority.GetSchedulePriorityParams
import co.softov.morestuff.android.domain.usecase.priority.GetSchedulePriorityUseCase
import co.softov.morestuff.android.presentation.model.PriorityOptionsModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class TaskPriorityViewModel(
    private val store: AppStore,
    private val getSchedulePriorityUseCase: GetSchedulePriorityUseCase,
    private val getPriorityOptionsUseCase: GetPriorityOptionsUseCase,
    private val taskId: Long,
) : ViewModel(), KoinComponent {

    private val _priorityOptions = MutableStateFlow(
        PriorityOptionsModel(
            current = Priority.today,
            options = listOf()
        )
    )
    val priorityOptions: StateFlow<PriorityOptionsModel> get() = _priorityOptions

    init {
        viewModelScope.launch {
            getSchedulePriorityUseCase(
                GetSchedulePriorityParams(taskId)
            ).map {
                mapPriorityOptionsResult(it)
            }
        }
    }

    private fun mapPriorityOptionsResult(it: PriorityOptionsResult) {
        _priorityOptions.value = PriorityOptionsModel(
            current = it.priority,
            options = it.options
        )
    }

    fun priorityChanged(priority: Priority) {
        viewModelScope.launch {
            val params = GetPriorityOptionsParams(
                priorityOptions.value.current,
                priority
            )
            getPriorityOptionsUseCase(params).map { mapPriorityOptionsResult(it) }
        }
    }

    fun onPriorityOptionChanged(option: PriorityOption) {
        store.dispatch(PriorityAction.SetCurrentPriorityOption(option))
    }

}

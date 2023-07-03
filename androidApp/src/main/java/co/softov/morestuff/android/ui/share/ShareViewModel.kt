package co.softov.morestuff.android.ui.share

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksUseCase
import co.softov.morestuff.android.ui.schedule.NotificationState
import co.softov.morestuff.android.ui.schedule.NotificationState.None
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ShareViewModel(
    private val getActiveTasksUseCase: GetActiveTasksUseCase,
) : NoStateViewModel() {

    override val enableDebug: Boolean
        get() = false

    init {
        loadData()
    }

    var tasks: List<TaskDomain> by mutableStateOf(listOf())
        private set

    var notification: NotificationState by mutableStateOf(None)
        private set

    override fun onLoadData() {
        getActiveTasksUseCase()
            .onEach { tasks = it }
            .launchIn(viewModelScope)
    }

}

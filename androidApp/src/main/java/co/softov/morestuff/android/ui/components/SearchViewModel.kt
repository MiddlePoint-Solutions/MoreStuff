package co.softov.morestuff.android.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksUseCase
import kotlinx.coroutines.flow.Flow

class SearchViewModel(
    private val getActiveTasksUseCase: GetActiveTasksUseCase,
    ) : NoStateViewModel() {
    var tasks: List<TaskDomain> by mutableStateOf(listOf())
        private set


}



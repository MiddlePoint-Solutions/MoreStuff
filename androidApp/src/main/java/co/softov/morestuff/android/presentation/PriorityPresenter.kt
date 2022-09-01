package co.softov.morestuff.android.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import co.softov.morestuff.android.domain.enums.*
import co.softov.morestuff.android.domain.redux.AppStore
import org.koin.androidx.compose.get

sealed interface PriorityModel {
    data class Today(val current: PriorityOption) : PriorityModel
    data class Tomorrow(val current: PriorityOption) : PriorityModel
    data class Later(val current: PriorityOption) : PriorityModel
}

@Composable
fun PriorityPresenter(store: AppStore = get()): PriorityModel {
    val priority by store.state.collectAsState()
    return when (val current = priority.priorityState.current) {
        is Priority.Later -> PriorityModel.Later(current.option)
        is Priority.Today -> PriorityModel.Today(current.option)
        is Priority.Tomorrow -> PriorityModel.Tomorrow(current.option)
    }
}
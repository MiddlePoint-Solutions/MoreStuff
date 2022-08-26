package co.softov.morestuff.android.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import co.softov.morestuff.android.domain.enums.LaterOption
import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.enums.TodayOption
import co.softov.morestuff.android.domain.enums.TomorrowOption
import co.softov.morestuff.android.domain.redux.AppStore
import org.koin.androidx.compose.get

sealed interface PriorityModel {
    data class Today(val current: TodayOption) : PriorityModel
    data class Tomorrow(val current: TomorrowOption) : PriorityModel
    data class Later(val current: LaterOption) : PriorityModel
}

@Composable
fun PriorityPresenter(store: AppStore = get()): PriorityModel {
    val priority by store.state.collectAsState()
    return when (priority.priorityState.current) {
        is Priority.Later -> PriorityModel.Later(LaterOption.Auto)
        is Priority.Today -> PriorityModel.Today(TodayOption.Auto)
        is Priority.Tomorrow -> PriorityModel.Tomorrow(TomorrowOption.Auto)
    }
}
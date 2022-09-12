package co.softov.morestuff.android.presentation

import androidx.compose.runtime.*
import co.softov.morestuff.android.domain.enums.*
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.currentPriority
import co.softov.morestuff.android.domain.redux.priorityState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.get

sealed interface PriorityModel {
    data class Today(val current: PriorityOption) : PriorityModel
    data class Tomorrow(val current: PriorityOption) : PriorityModel
    data class Later(val current: PriorityOption) : PriorityModel
}

fun Priority.mapToModel() = when (this) {
    is Priority.Later -> PriorityModel.Later(option)
    is Priority.Today -> PriorityModel.Today(option)
    is Priority.Tomorrow -> PriorityModel.Tomorrow(option)
}

// TODO: Test this implementation (How many times does it get recomposed?)
@Composable
fun PriorityPresenter(store: AppStore = get()): PriorityModel {

    var model: PriorityModel by remember {
        val initial = store.priorityState.current.mapToModel()
        mutableStateOf(initial)
    }

    val priorityFlow by remember {
        store.state.map { it.priorityState.current.mapToModel() }
    }.collectAsState(
        initial = model
    )

    model = priorityFlow
    return model
}
package co.softov.morestuff.android.presentation.presenter

import androidx.compose.runtime.*
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.priorityState
import co.softov.morestuff.android.presentation.model.PriorityModel
import co.softov.morestuff.android.presentation.model.mapToModel
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.get
import timber.log.Timber

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

    Timber.d("priorityFlow: $priorityFlow")

    model = priorityFlow
    return model
}
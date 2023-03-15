package co.softov.morestuff.android.presentation.presenter

import androidx.compose.runtime.*
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.priorityState
import co.softov.morestuff.android.presentation.model.PriorityOptionsModel
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.get

@Composable
fun PriorityOptionsPresenter(
    store: AppStore = get()
): PriorityOptionsModel {

    var model: PriorityOptionsModel by remember {
        mutableStateOf(
            PriorityOptionsModel(
                current = store.priorityState.current,
                options = store.priorityState.options
            )
        )
    }

    val optionFlow by remember {
        store.state.map {
            PriorityOptionsModel(
                current = store.priorityState.current,
                options = store.priorityState.options
            )
        }
    }.collectAsState(
        initial = model
    )

    model = optionFlow
    return model
}
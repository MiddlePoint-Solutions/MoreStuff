package co.softov.morestuff.android.presentation.presenter

import androidx.compose.runtime.*
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.ui.list.model.ScheduleListItemMapper
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel
import org.koin.androidx.compose.get
import timber.log.Timber

data class ReviewModel(
    val items: List<ScheduleListItemViewModel> = listOf()
)

private val scheduleItemMapper by lazy { ScheduleListItemMapper() }

//@Composable
//fun ReviewPresenter(schedulesFlow: Flow<List<ScheduleWithTitle>>): ReviewModel {
//    val schedules by schedulesFlow.collectAsState(initial = listOf())
//    Timber.d("new Model available: ${schedules.size}")
//    return ReviewModel(items = schedules.map(scheduleItemMapper::map))
//}

@Composable
fun ReviewPresenter(
    store: AppStore = get(),
    mapper: ScheduleListItemMapper = ScheduleListItemMapper()
): ReviewModel {

    val currentState = store.state.collectAsState()
    var model by remember {
        mutableStateOf(ReviewModel(currentState.value.reviewState.schedules.map(mapper::map)))
    }
    model = model.copy(items = currentState.value.reviewState.schedules.map(mapper::map))


    Timber.d("ReviewPresenter: $model")

    return model
}
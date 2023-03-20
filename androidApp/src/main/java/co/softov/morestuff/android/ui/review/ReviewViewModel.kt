package co.softov.morestuff.android.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.state.ReviewAction
import co.softov.morestuff.android.presentation.presenter.ReviewModel
import co.softov.morestuff.android.ui.list.model.ScheduleListItemMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class ReviewViewModel : ViewModel(), KoinComponent {

    private val store: AppStore by inject()
    private val mapper = ScheduleListItemMapper()

    private var _model: MutableStateFlow<ReviewModel> = MutableStateFlow(ReviewModel())
    val model: StateFlow<ReviewModel> get() = _model

    init {
        store.dispatch(ReviewAction.InitReview)
        viewModelScope.launch {
            store.state.onEach {
                _model.value = ReviewModel(items = it.reviewState.schedules.map(mapper::map))
                Timber.d("New state: $it")
            }.launchIn(this)
        }
    }

    fun onLoad() {


    }

}
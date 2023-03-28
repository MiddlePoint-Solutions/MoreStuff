package co.softov.morestuff.android.ui.review

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.usecase.schedule.GetSchedulesWithTitleUseCase
import co.softov.morestuff.android.presentation.presenter.PriorityReviewModel
import co.softov.morestuff.android.presentation.presenter.PriorityRound
import co.softov.morestuff.android.ui.list.model.ScheduleListItemMapper
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel
import co.softov.morestuff.android.ui.review.swipeable.Direction
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class PriorityReviewViewModel(
    private val getSchedulesWithTitle: GetSchedulesWithTitleUseCase
) : NoStateViewModel() {

    private val mapper = ScheduleListItemMapper()

    private var _model = MutableStateFlow(PriorityReviewModel())
    val model: StateFlow<PriorityReviewModel> get() = _model

    private val priorityTasks = mutableListOf<ScheduleListItemViewModel>()

    init {
        setInitialState()
    }

    private fun setInitialState(round: PriorityRound = PriorityRound.Initial) {
        viewModelScope.launch {
            val schedules = getSchedulesWithTitle().map(mapper::map).shuffled()
            _model.value = PriorityReviewModel(items = schedules, round = round)
        }
    }

    fun reset() {
        setInitialState(round = PriorityRound.Next)
    }

    fun onTaskSwiped(
        schedule: ScheduleListItemViewModel,
        direction: Direction,
        isLast: Boolean
    ) {
        Timber.d("Schedule: ${schedule.taskTitle}, swiped: $direction, isLast: $isLast")
        when (direction) {
            Direction.Left -> {}
            Direction.Right -> priorityTasks.add(schedule)
            Direction.Up -> {}
            Direction.Down -> {}
        }

        if (isLast) {
            val nextRound = when {
                priorityTasks.size >= NEXT_ROUND_MINIMUM -> PriorityRound.Next
                priorityTasks.size > 0 -> PriorityRound.Final
                else -> PriorityRound.Final // TODO: suggest random task?
            }
            setupNextRound(nextRound)
        }
    }

    private fun setupNextRound(round: PriorityRound) {
        viewModelScope.launch {
            _model.update {
                it.copy(
                    number = it.number + 1,
                    round = round,
                    items = priorityTasks.toMutableList().shuffled()
                ).also { next ->
                    Timber.d("round: ${next.round}, number: ${next.number}")
                }
            }
        }.invokeOnCompletion {
            priorityTasks.clear()
        }
    }

    companion object {
        const val NEXT_ROUND_MINIMUM = 3
    }

}
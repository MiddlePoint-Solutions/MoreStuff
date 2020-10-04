package co.softov.morestuff.androidApp.presentation.dashboard

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import co.softov.morestuff.androidApp.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.androidApp.domain.enums.Priority.Later
import co.softov.morestuff.androidApp.domain.enums.Priority.Today
import co.softov.morestuff.androidApp.domain.enums.Priority.Tomorrow
import co.softov.morestuff.androidApp.domain.repository.PreferenceRepository
import co.softov.morestuff.androidApp.domain.usecase.task.CreateTask
import co.softov.morestuff.androidApp.domain.usecase.task.TaskParams
import co.softov.morestuff.androidApp.presentation.dashboard.DashboardViewEvent.ChangePriority
import co.softov.morestuff.androidApp.presentation.dashboard.DashboardViewEvent.Init
import co.softov.morestuff.androidApp.presentation.dashboard.DashboardViewEvent.SetCurrentOption
import co.softov.morestuff.androidApp.presentation.dashboard.DashboardViewEvent.SetCustomTime
import co.softov.morestuff.androidApp.presentation.dashboard.options.TimeOption
import co.softov.morestuff.androidApp.presentation.dashboard.options.TimeOption.CUSTOM
import co.softov.morestuff.androidApp.presentation.dashboard.options.createLaterOptions
import co.softov.morestuff.androidApp.presentation.dashboard.options.createTodayOptions
import co.softov.morestuff.androidApp.presentation.dashboard.options.createTomorrowOptions
import java.util.Calendar

class DashboardViewModel(
    private val preferenceRepository: PreferenceRepository,
    private val createNewTask: CreateTask,
    private val conductor: DashboardConductor
) : BaseViewModel<DashboardViewState, DashboardViewEvent>(DashboardViewState()) {

    private val todayOptions: List<TimeOption> =
        TimeOption.getFilteredTodayOptions(Calendar.getInstance().getHourOfDay())

    private val tomorrowOptions = TimeOption.TOMORROW
    private val laterOptions = TimeOption.LATER

    override fun onLoadData() {
        sendEvent(Init)
    }

    override fun onReduceState(event: DashboardViewEvent): DashboardViewState {
        return when (event) {
            is Init -> {
                state.copy(items = todayOptions.createTodayOptions())
            }

            is SetCurrentOption -> {
                val itemId = state.items.indexOfFirst { it.option == event.option }.toLong()
                state.copy(currentItemId = itemId)
            }

            is SetCustomTime -> {
                val itemId = state.items.indexOfFirst { it.option == CUSTOM }.toLong()
                val items = state.items.map { optionItem ->
                    if (optionItem.option == CUSTOM) {
                        optionItem.copy(time = event.time)
                    } else optionItem
                }
                state.copy(currentItemId = itemId, items = items)
            }

            is ChangePriority -> {
                val priorityItems = when (event.priority) {
                    is Later -> laterOptions.createLaterOptions()
                    is Today -> todayOptions.createTodayOptions()
                    is Tomorrow -> tomorrowOptions.createTomorrowOptions()
                }
                state.copy(
                    currentItemId = FIRST_ITEM_ID,
                    priority = event.priority,
                    items = priorityItems
                )
            }
        }
    }

    fun userSetCustomTime(time: Long) {
        sendEvent(SetCustomTime(time))
    }

    fun userSelectedOption(optionId: Long) {
        when (val option = state.items[optionId.toInt()].option) {
            CUSTOM -> when (state.priority) {
                is Later -> conductor.showDateTimePicker()
                is Today -> conductor.showTodayTimePicker()
                is Tomorrow -> conductor.showTomorrowTimePicker()
            }
            else -> sendEvent(SetCurrentOption(option))
        }
    }

    fun selectedTodayPriority() {
        sendEvent(ChangePriority(Today()))
    }

    fun selectedTomorrowPriority() {
        sendEvent(ChangePriority(Tomorrow()))
    }

    fun selectedLaterPriority() {
        sendEvent(ChangePriority(Later()))
    }

    fun createTask(title: String) {
        GlobalScope.launch {
            val scheduleTime = state.items[state.currentItemId.toInt()].time
            val params = TaskParams(title, scheduleTime, state.priority)
            createNewTask(params)
            cancel()
        }
        conductor.showTaskList()
    }

    companion object {
        private const val FIRST_ITEM_ID: Long = 0
    }
}
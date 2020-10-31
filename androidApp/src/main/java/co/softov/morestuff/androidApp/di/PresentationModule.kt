package co.softov.morestuff.androidApp.di

import co.softov.morestuff.androidApp.app.presentation.fragment.FlowFragmentFactory
import co.softov.morestuff.androidApp.presentation.content.ContentConductor
import co.softov.morestuff.androidApp.presentation.content.ContentViewModel
import co.softov.morestuff.androidApp.presentation.list.schedule.all.AllScheduleViewModel
import co.softov.morestuff.androidApp.presentation.list.schedule.later.LaterScheduleViewModel
import co.softov.morestuff.androidApp.presentation.list.schedule.today.TodayScheduleViewModel
import co.softov.morestuff.androidApp.presentation.list.schedule.tomorrow.TomorrowScheduleViewModel
import co.softov.morestuff.androidApp.presentation.list.tasks.active.ActiveTasksViewModel
import co.softov.morestuff.androidApp.presentation.list.tasks.complete.CompleteTasksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    // ViewModel
    viewModel { (conductor: ContentConductor) ->
        ContentViewModel(
            conductor = conductor,
            getPagedMessages = get()
        )
    }
    viewModel {
        ActiveTasksViewModel(
            getActiveTasks = get(),
            rescheduleTask = get(),
            setTaskComplete = get()
        )
    }
    viewModel {
        CompleteTasksViewModel(
            getCompleteTasks = get(),
            rescheduleTask = get(),
            setTaskComplete = get()
        )
    }
    viewModel {
        TodayScheduleViewModel(
            getSchedulesWithTitle = get(),
            rescheduleTask = get(),
            setTaskComplete = get()
        )
    }
    viewModel {
        TomorrowScheduleViewModel(
            getSchedulesWithTitle = get(),
            rescheduleTask = get(),
            setTaskComplete = get()
        )
    }
    viewModel {
        LaterScheduleViewModel(
            getLaterSchedules = get(),
            rescheduleTask = get(),
            setTaskComplete = get()
        )
    }
    viewModel {
        AllScheduleViewModel(
            getSchedulesWithTitle = get(),
            rescheduleTask = get(),
            setTaskComplete = get()
        )
    }
}
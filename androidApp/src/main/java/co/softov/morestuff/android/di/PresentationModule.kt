package co.softov.morestuff.android.di

import co.softov.morestuff.android.presentation.content.ContentConductor
import co.softov.morestuff.android.presentation.content.ContentViewModel
import co.softov.morestuff.android.presentation.list.Page
import co.softov.morestuff.android.presentation.list.SchedulePageViewModel
import co.softov.morestuff.android.presentation.list.schedule.all.AllScheduleViewModel
import co.softov.morestuff.android.presentation.list.schedule.later.LaterScheduleViewModel
import co.softov.morestuff.android.presentation.list.schedule.today.TodayScheduleViewModel
import co.softov.morestuff.android.presentation.list.schedule.tomorrow.TomorrowScheduleViewModel
import co.softov.morestuff.android.presentation.settings.SettingsViewModel
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
        TodayScheduleViewModel(
            getSchedulesWithTitle = get()
        )
    }
    viewModel {
        TomorrowScheduleViewModel(
            getSchedulesWithTitle = get()
        )
    }
    viewModel {
        LaterScheduleViewModel(
            getLaterSchedules = get()
        )
    }
    viewModel {
        AllScheduleViewModel(
            getSchedulesWithTitle = get()
        )
    }

    viewModel { SettingsViewModel() }

    viewModel { (page: Page) ->
        SchedulePageViewModel(
            page = page,
            getSchedules = get(),
            getLaterSchedules = get(),
            getActiveTasks = get(),
            getCompleteTasks = get(),
            getTodaySchedules = get(),
            getTomorrowSchedules = get()
        )
    }
}
package co.softov.morestuff.android.di

import co.softov.morestuff.android.ui.chat.TaskChatViewModel
import co.softov.morestuff.android.ui.main.MainConductor
import co.softov.morestuff.android.ui.main.MainViewModel
import co.softov.morestuff.android.ui.list.SchedulePageViewModel
import co.softov.morestuff.android.ui.list.model.PageType
import co.softov.morestuff.android.ui.priority.PriorityViewModel
import co.softov.morestuff.android.ui.review.ReviewViewModel
import co.softov.morestuff.android.ui.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {

    viewModelOf(::MainViewModel)

    viewModel { SettingsViewModel() }

    viewModel { (page: PageType) ->
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

    viewModelOf(::ReviewViewModel)
    viewModelOf(::PriorityViewModel)
    viewModelOf(::TaskChatViewModel)
}
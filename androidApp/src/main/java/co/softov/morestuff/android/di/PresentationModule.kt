package co.softov.morestuff.android.di

import co.softov.morestuff.android.ui.chat.task.TaskChatViewModel
import co.softov.morestuff.android.ui.drawer.DrawerViewModel
import co.softov.morestuff.android.ui.list.SchedulePageViewModel
import co.softov.morestuff.android.ui.home.HomeViewModel
import co.softov.morestuff.android.ui.model.PageType
import co.softov.morestuff.android.ui.model.map.ReviewItemMapper
import co.softov.morestuff.android.ui.priority.PlanViewModel
import co.softov.morestuff.android.ui.priority.TaskPriorityViewModel
import co.softov.morestuff.android.ui.review.ReviewViewModel
import co.softov.morestuff.android.ui.schedule.PriorityViewModel
import co.softov.morestuff.android.ui.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val presentationModule = module {


    viewModelOf(::HomeViewModel)
    viewModelOf(::DrawerViewModel)
    viewModelOf(::SettingsViewModel)

    viewModel { (page: PageType) ->
        SchedulePageViewModel(
            page = page,
            getCompleteTasks = get(),
            timeFormatter = get(),
            timeManager = get(),
            getLaterTaskUseCase = get(),
            getNowTaskUseCase = get(),
            getSchedules = get()
        )
    }

    viewModelOf(::ReviewViewModel)
    viewModelOf(::PriorityViewModel)
    viewModelOf(::TaskPriorityViewModel)
    viewModelOf(::TaskChatViewModel)
    viewModelOf(::PlanViewModel)

    factoryOf(::ReviewItemMapper)
}
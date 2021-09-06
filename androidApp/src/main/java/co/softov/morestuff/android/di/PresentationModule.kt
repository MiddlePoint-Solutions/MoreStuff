package co.softov.morestuff.android.di

import co.softov.morestuff.android.data.mapper.makeMessageDbMapper
import co.softov.morestuff.android.presentation.content.ContentConductor
import co.softov.morestuff.android.presentation.content.ContentViewModel
import co.softov.morestuff.android.presentation.schedule_list.SchedulePageViewModel
import co.softov.morestuff.android.presentation.schedule_list.model.PageType
import co.softov.morestuff.android.presentation.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    // ViewModel
    viewModel { (conductor: ContentConductor) ->
        ContentViewModel(
            conductor = conductor,
            messageMap = makeMessageDbMapper(),
            getPagedMessages = get()
        )
    }

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
}
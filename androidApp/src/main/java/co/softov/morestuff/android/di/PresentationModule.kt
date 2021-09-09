package co.softov.morestuff.android.di

import co.softov.morestuff.android.data.mapper.makeMessageDbMapper
import co.softov.morestuff.android.ui.main.ContentConductor
import co.softov.morestuff.android.ui.main.ContentViewModel
import co.softov.morestuff.android.ui.list.SchedulePageViewModel
import co.softov.morestuff.android.ui.list.model.PageType
import co.softov.morestuff.android.ui.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    // ViewModel
    viewModel { (conductor: ContentConductor) ->
        ContentViewModel(
            conductor = conductor,
            messageMap = makeMessageDbMapper(),
            getPagedMessages = get(),
            getMessages = get()
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
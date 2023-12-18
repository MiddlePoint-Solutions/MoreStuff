package co.softov.morestuff.android.di

import co.softov.morestuff.android.ui.chat.task.TaskChatViewModel
import co.softov.morestuff.android.ui.home.HomeViewModel
import co.softov.morestuff.android.ui.input.UserInputViewModel
import co.softov.morestuff.android.ui.input.VoiceToTextViewModel
import co.softov.morestuff.android.ui.main.MainViewModel
import co.softov.morestuff.android.ui.model.map.MessageUiMapper
import co.softov.morestuff.android.ui.model.map.ReviewTasksMapper
import co.softov.morestuff.android.ui.model.map.TaskUiMapper
import co.softov.morestuff.android.ui.priority.PlanViewModel
import co.softov.morestuff.android.ui.review.ReviewViewModel
import co.softov.morestuff.android.ui.schedule.ScopeViewModel
import co.softov.morestuff.android.ui.search.SearchViewModel
import co.softov.morestuff.android.ui.settings.SettingsViewModel
import co.softov.morestuff.android.ui.share.ShareViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::UserInputViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::ReviewViewModel)
    viewModelOf(::ScopeViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::TaskChatViewModel)
    viewModelOf(::PlanViewModel)
    viewModelOf(::ShareViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::VoiceToTextViewModel)

    factoryOf(::ReviewTasksMapper)
    factoryOf(::TaskUiMapper)
    factoryOf(::MessageUiMapper)
}
package io.middlepoint.morestuff.shared.di

import io.middlepoint.morestuff.shared.ui.components.input.UserInputViewModel
import io.middlepoint.morestuff.shared.ui.components.input.voice.VoiceToTextViewModel
import io.middlepoint.morestuff.shared.ui.model.map.MessageUiMapper
import io.middlepoint.morestuff.shared.ui.model.map.ReviewTasksMapper
import io.middlepoint.morestuff.shared.ui.model.map.ScopeUiMapper
import io.middlepoint.morestuff.shared.ui.model.map.TaskUiMapper
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatPresenter
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskDetailsViewModel
import io.middlepoint.morestuff.shared.ui.screen.home.AppPresenter
import io.middlepoint.morestuff.shared.ui.screen.home.HomeViewModel
import io.middlepoint.morestuff.shared.ui.screen.main.MainViewModel
import io.middlepoint.morestuff.shared.ui.screen.review.ReviewViewModel
import io.middlepoint.morestuff.shared.ui.screen.schedule.ScopeTasksViewModel
import io.middlepoint.morestuff.shared.ui.screen.scopes.ScopesViewModel
import io.middlepoint.morestuff.shared.ui.screen.search.SearchViewModel
import io.middlepoint.morestuff.shared.ui.screen.settings.SettingsViewModel
import io.middlepoint.morestuff.shared.ui.screen.share.ShareViewModel
import org.koin.core.module.dsl.*
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::TaskChatPresenter)
    viewModelOf(::TaskDetailsViewModel)

    viewModelOf(::UserInputViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::ReviewViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::ScopeTasksViewModel)

    viewModelOf(::ShareViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::VoiceToTextViewModel)
    viewModelOf(::ScopesViewModel)

    singleOf(::AppPresenter)
    factoryOf(::ReviewTasksMapper)
    factoryOf(::TaskUiMapper)
    factoryOf(::ScopeUiMapper)
    factoryOf(::MessageUiMapper)
}

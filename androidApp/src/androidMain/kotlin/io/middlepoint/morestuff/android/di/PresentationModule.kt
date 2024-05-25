package io.middlepoint.morestuff.android.di

import io.middlepoint.morestuff.android.app.service.NotifierImpl
import io.middlepoint.morestuff.android.app.service.SchedulerImpl
import io.middlepoint.morestuff.android.ui.chat.task.TaskChatPresenter
import io.middlepoint.morestuff.android.ui.chat.task.TaskDetailsViewModel
import io.middlepoint.morestuff.android.ui.home.AppPresenter
import io.middlepoint.morestuff.android.ui.home.HomePresenter
import io.middlepoint.morestuff.android.ui.input.UserInputViewModel
import io.middlepoint.morestuff.android.ui.input.voice.VoiceToTextViewModel
import io.middlepoint.morestuff.android.ui.main.MainViewModel
import io.middlepoint.morestuff.android.ui.model.map.MessageUiMapper
import io.middlepoint.morestuff.android.ui.model.map.ReviewTasksMapper
import io.middlepoint.morestuff.android.ui.model.map.TaskUiMapper
import io.middlepoint.morestuff.android.ui.review.ReviewViewModel
import io.middlepoint.morestuff.android.ui.schedule.ScopeTasksPresenter
import io.middlepoint.morestuff.android.ui.scopes.ScopesViewModel
import io.middlepoint.morestuff.android.ui.search.SearchViewModel
import io.middlepoint.morestuff.android.ui.settings.SettingsViewModel
import io.middlepoint.morestuff.android.ui.share.ShareViewModel
import io.middlepoint.morestuff.shared.domain.service.Notifier
import io.middlepoint.morestuff.shared.domain.service.Scheduler
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::UserInputViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::ReviewViewModel)
    viewModelOf(::HomePresenter)
    viewModelOf(::ScopeTasksPresenter)
    viewModelOf(::TaskChatPresenter)
    viewModelOf(::ShareViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::VoiceToTextViewModel)
    viewModelOf(::TaskDetailsViewModel)
    viewModelOf(::ScopesViewModel)
    singleOf(::AppPresenter)

    factoryOf(::ReviewTasksMapper)
    factoryOf(::TaskUiMapper)
    factoryOf(::MessageUiMapper)

    singleOf(::SchedulerImpl) bind Scheduler::class
    singleOf(::NotifierImpl) bind Notifier::class
}

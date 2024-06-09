package io.middlepoint.morestuff.android.di

import io.middlepoint.morestuff.android.app.service.NotifierImpl
import io.middlepoint.morestuff.android.app.service.SchedulerImpl
import io.middlepoint.morestuff.android.ui.home.AppPresenter
import io.middlepoint.morestuff.android.ui.home.HomePresenter
import io.middlepoint.morestuff.android.ui.main.MainViewModel
import io.middlepoint.morestuff.android.ui.schedule.ScopeTasksPresenter
import io.middlepoint.morestuff.android.ui.scopes.ScopesViewModel
import io.middlepoint.morestuff.android.ui.search.SearchViewModel
import io.middlepoint.morestuff.android.ui.settings.SettingsViewModel
import io.middlepoint.morestuff.android.ui.share.ShareViewModel
import io.middlepoint.morestuff.shared.domain.service.Notifier
import io.middlepoint.morestuff.shared.domain.service.Scheduler
import io.middlepoint.morestuff.shared.ui.components.input.UserInputViewModel
import io.middlepoint.morestuff.shared.ui.components.input.voice.VoiceToTextViewModel
import io.middlepoint.morestuff.shared.ui.model.map.MessageUiMapper
import io.middlepoint.morestuff.shared.ui.model.map.ReviewTasksMapper
import io.middlepoint.morestuff.shared.ui.model.map.TaskUiMapper
import io.middlepoint.morestuff.shared.ui.screen.review.ReviewViewModel
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


    viewModelOf(::ShareViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::VoiceToTextViewModel)
    viewModelOf(::ScopesViewModel)
    singleOf(::AppPresenter)

    factoryOf(::ReviewTasksMapper)
    factoryOf(::TaskUiMapper)
    factoryOf(::MessageUiMapper)

    singleOf(::SchedulerImpl) bind Scheduler::class
    singleOf(::NotifierImpl) bind Notifier::class
}

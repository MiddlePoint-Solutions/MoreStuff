package co.softov.morestuff.android.di

import co.softov.morestuff.android.ui.MainViewModel
import co.softov.morestuff.android.ui.chat.task.TaskChatViewModel
import co.softov.morestuff.android.ui.drawer.DrawerViewModel
import co.softov.morestuff.android.ui.home.MainChatViewModel
import co.softov.morestuff.android.ui.input.UserInputViewModel
import co.softov.morestuff.android.ui.components.SearchViewModel
import co.softov.morestuff.android.ui.model.PageType
import co.softov.morestuff.android.ui.model.map.ReviewItemMapper
import co.softov.morestuff.android.ui.priority.PlanViewModel
import co.softov.morestuff.android.ui.review.ReviewViewModel
import co.softov.morestuff.android.ui.schedule.PriorityViewModel
import co.softov.morestuff.android.ui.settings.SettingsViewModel
import co.softov.morestuff.android.ui.share.ShareViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val presentationModule = module {

    viewModelOf(::UserInputViewModel)
    viewModelOf(::MainChatViewModel)
    viewModelOf(::DrawerViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::MainViewModel)

    viewModelOf(::ReviewViewModel)
    viewModelOf(::PriorityViewModel)
    viewModelOf(::TaskChatViewModel)
    viewModelOf(::PlanViewModel)
    viewModelOf(::ShareViewModel)
    viewModelOf(::SearchViewModel)

    factoryOf(::ReviewItemMapper)
}
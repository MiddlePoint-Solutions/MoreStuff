package io.middlepoint.morestuff.shared.di

import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatPresenter
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskDetailsViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::TaskChatPresenter)
    viewModelOf(::TaskDetailsViewModel)
}

package io.middlepoint.morestuff.shared.di

import io.middlepoint.morestuff.shared.data.service.AppMessagesProviderImpl
import io.middlepoint.morestuff.shared.data.service.HintTaskProviderImpl
import io.middlepoint.morestuff.shared.data.service.OpenGraphFetcherImpl
import io.middlepoint.morestuff.shared.data.service.TimeManagerImpl
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.data.middleware.AuthMiddleware
import io.middlepoint.morestuff.shared.domain.redux.middleware.DevMiddleware
import io.middlepoint.morestuff.shared.domain.redux.middleware.ErrorMiddleware
import io.middlepoint.morestuff.shared.domain.redux.middleware.LoggerMiddleware
import io.middlepoint.morestuff.shared.domain.redux.middleware.MessageMiddleware
import io.middlepoint.morestuff.shared.domain.redux.middleware.NotificationMiddleware
import io.middlepoint.morestuff.shared.domain.redux.middleware.PriorityMiddleware
import io.middlepoint.morestuff.shared.domain.redux.middleware.ReminderMiddleware
import io.middlepoint.morestuff.shared.domain.redux.middleware.ScheduleMiddleware
import io.middlepoint.morestuff.shared.domain.redux.middleware.ScopeMiddleware
import io.middlepoint.morestuff.shared.domain.redux.middleware.SettingsMiddleware
import io.middlepoint.morestuff.shared.domain.redux.middleware.TaskMiddleware
import io.middlepoint.morestuff.shared.domain.service.AppMessageProvider
import io.middlepoint.morestuff.shared.domain.service.HintTaskProvider
import io.middlepoint.morestuff.shared.domain.service.OpenGraphFetcher
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import io.middlepoint.morestuff.shared.domain.usecase.message.CheckForUrlMetadataUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.CheckForUrlMetadataUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.message.ClearActivePendingMessagesUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.message.ClearActiveReminderMessagesUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.CountActiveReminderMessagesUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.CountActiveReminderMessagesUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateMediaMessageUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateMediaMessageUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateMessageUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateMessageUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.message.CreatePDFMessageUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.CreatePDFMessageUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateScheduleMessageUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateScheduleMessageUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateTaskConfirmationMessageUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateTaskConfirmationMessageUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.message.DeleteMessageUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.DeleteMessageUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.message.FetchOpenGraphMetadataUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.FetchOpenGraphMetadataUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.message.GetLastMessageFlowUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.GetLastMessageFlowUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.message.GetMessageImpl
import io.middlepoint.morestuff.shared.domain.usecase.message.GetMessageUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.GetMessagesUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.GetMessagesUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.message.GetTaskChatMessagesUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.GetTaskChatMessagesUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.message.GetTaskMessagesFlowUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.GetTaskMessagesFlowUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.message.SaveMediaUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.SaveMediaUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.message.SaveUserPDFUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.SaveUserPDFUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.message.SetScheduleMessageResponseUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.SetScheduleMessageResponseUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.message.UpdateMessageContentUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.UpdateMessageContentUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetDefaultPriorityScoreUseCase
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetDefaultPriorityScoreUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetHighestPriorityScoreUseCase
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetHighestPriorityScoreUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetLowestPriorityScoreUseCase
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetLowestPriorityScoreUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetPlanPriorityScoreUseCase
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetPlanPriorityScoreUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetTaskAbovePriorityScoreUseCase
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetTaskAbovePriorityScoreUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetTaskBelowPriorityScoreUseCase
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetTaskBelowPriorityScoreUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.priority.UpdateTaskReviewPriorityUseCase
import io.middlepoint.morestuff.shared.domain.usecase.priority.UpdateTaskReviewPriorityUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.schedule.BootCompleteSchedulerUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.BootCompleteSchedulerUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.schedule.CancelActiveScheduleUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.CancelActiveScheduleUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.schedule.CreateOneTimeScheduleUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.CreateOneTimeScheduleUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.schedule.CreateReminderUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.CreateReminderUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.schedule.CreateScheduleUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.CreateScheduleUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.schedule.GetActiveScheduleFlowUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.GetActiveScheduleFlowUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.schedule.GetActiveSchedulesUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.GetActiveSchedulesUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.schedule.GetAllActiveSchedulesUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.GetAllActiveSchedulesUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.schedule.GetScheduleImpl
import io.middlepoint.morestuff.shared.domain.usecase.schedule.GetScheduleUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.GetTaskScheduleCountUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.GetTaskScheduleCountUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.schedule.ScheduleAtTimeUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.ScheduleAtTimeUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.schedule.ScheduleWorkUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.ScheduleWorkUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.schedule.SetScheduleFulfilledUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.SetScheduleFulfilledUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.schedule.ToggleQuickReminderUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.ToggleQuickReminderUseCaseImpl

import io.middlepoint.morestuff.shared.domain.usecase.scope.CreateScopeUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.CreateScopeUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.scope.DeleteScopeUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.DeleteScopeUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.scope.GetScopeByTaskIdUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.GetScopeByTaskIdUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.scope.GetScopesFlowUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.GetScopesFlowUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.scope.GetScopesUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.GetScopesUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.scope.InitScopesUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.InitScopesUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.scope.UpdateScopeNameUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.UpdateScopeNameUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.scope.UpdateScopesOrderUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.UpdateScopesOrderUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.settings.CheckFirstTimeUseCase
import io.middlepoint.morestuff.shared.domain.usecase.settings.CheckFirstTimeUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.settings.GetAppSettingUseCase
import io.middlepoint.morestuff.shared.domain.usecase.settings.GetAppSettingUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.settings.GetAppSettingsUseCase
import io.middlepoint.morestuff.shared.domain.usecase.settings.GetAppSettingsUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.settings.GetAppThemeUseCase
import io.middlepoint.morestuff.shared.domain.usecase.settings.GetAppThemeUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.settings.SaveUserSettingUseCase
import io.middlepoint.morestuff.shared.domain.usecase.settings.SaveUserSettingUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.AddTasksToScopeUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.AddTasksToScopeUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.ClearTaskNotificationsUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.ClearTaskNotificationsUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.CreateHintTaskUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.CreateHintTaskUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.CreateNewTaskUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.CreateTaskUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.DecreaseTaskPriorityScoreUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.DecrementTaskPriorityScoreUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.DeleteTasksUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.DeleteTasksUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.GetActiveTasksFlowUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.GetActiveTasksFlowUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.GetActiveTasksWithScheduleUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.GetActiveTasksWithScheduleUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.GetCompletedTasksUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.GetCompletedTasksUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.GetReviewTasksUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.GetReviewTasksUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.GetScopeActiveTasksFlowUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.GetScopeActiveTasksFlowUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskFlowUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskFlowUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskForScheduleUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskForScheduleUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTasksByIdsUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTasksByIdsUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTasksWithoutScheduleUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTasksWithoutScheduleUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.IncreaseTaskPriorityScoreUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.IncrementTaskPriorityScoreUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.RemoveTasksFromScopeUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.RemoveTasksFromScopeUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.ReorderTaskUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.ReorderTaskUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.SearchTasksUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.SearchTasksUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.SetTaskCompleteImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.SetTaskCompleteUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.UpdatePlannedTasksPriorityUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.UpdatePlannedTasksPriorityUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.UpdateTaskPriorityScoreUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.UpdateTaskPriorityScoreUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.UpdateTaskTitleUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.UpdateTaskTitleUseCaseImpl
import io.middlepoint.morestuff.shared.domain.usecase.task.UpdateTasksScopeUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.UpdateTasksScopeUseCaseImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModules
    get() = buildList {
        add(storeModule)
        add(serviceModule)
        addAll(useCaseModules)
        add(timeManagerModule)
    }

val useCaseModules
    get() = buildList {
        add(taskUseCases)
        add(scheduleUseCases)
        add(messageUseCases)
        add(settingsUseCases)
        add(scopeUseCases)
    }

val serviceModule = module {
    factoryOf(::BootCompleteSchedulerUseCaseImpl) bind BootCompleteSchedulerUseCase::class
    factoryOf(::AppMessagesProviderImpl) bind AppMessageProvider::class
    factoryOf(::OpenGraphFetcherImpl) bind OpenGraphFetcher::class
}

val storeModule = module {
    // Store

    single(createdAtStart = true) {
        AppStore(
            logger = get(),
            devMiddleware = get(),
            errorMiddleware = get(),
            taskMiddleware = get(),
            messageMiddleware = get(),
            scheduleMiddleware = get(),
            responseMiddleware = get(),
            notificationMiddleware = get(),
            settingsMiddleware = get(),
            priorityMiddleware = get(),
            scopeMiddleware = get(),
            authMiddleware = get()
        )
    }

    // Middleware
    factoryOf(::LoggerMiddleware)
    factoryOf(::TaskMiddleware)
    factoryOf(::ScheduleMiddleware)
    factoryOf(::MessageMiddleware)
    factoryOf(::NotificationMiddleware)
    factoryOf(::SettingsMiddleware)
    factoryOf(::ReminderMiddleware)
    factoryOf(::ErrorMiddleware)
    factoryOf(::AuthMiddleware)
    factoryOf(::PriorityMiddleware)
    factoryOf(::DevMiddleware)
    factoryOf(::ScopeMiddleware)

}

val taskUseCases = module {
    factoryOf(::CreateNewTaskUseCaseImpl) bind CreateTaskUseCase::class
    factoryOf(::GetTaskUseCaseImpl) bind GetTaskUseCase::class
    factoryOf(::GetTaskFlowUseCaseImpl) bind GetTaskFlowUseCase::class
    factoryOf(::GetTaskMessagesFlowUseCaseImpl) bind GetTaskMessagesFlowUseCase::class
    factoryOf(::GetActiveTasksFlowUseCaseImpl) bind GetActiveTasksFlowUseCase::class
    factoryOf(::GetScopeActiveTasksFlowUseCaseImpl) bind GetScopeActiveTasksFlowUseCase::class
    factoryOf(::GetCompletedTasksUseCaseImpl) bind GetCompletedTasksUseCase::class
    factoryOf(::SetTaskCompleteImpl) bind SetTaskCompleteUseCase::class
    factoryOf(::UpdateTaskTitleUseCaseImpl) bind UpdateTaskTitleUseCase::class
    factoryOf(::GetTaskForScheduleUseCaseImpl) bind GetTaskForScheduleUseCase::class
    factoryOf(::GetTasksWithoutScheduleUseCaseImpl) bind GetTasksWithoutScheduleUseCase::class
    factoryOf(::GetActiveTasksWithScheduleUseCaseImpl) bind GetActiveTasksWithScheduleUseCase::class
    factoryOf(::SearchTasksUseCaseImpl) bind SearchTasksUseCase::class
    factoryOf(::CreateHintTaskUseCaseImpl) bind CreateHintTaskUseCase::class
    factoryOf(::HintTaskProviderImpl) bind HintTaskProvider::class
    factoryOf(::DeleteTasksUseCaseImpl) bind DeleteTasksUseCase::class
    factoryOf(::AddTasksToScopeUseCaseImpl) bind AddTasksToScopeUseCase::class
    factoryOf(::RemoveTasksFromScopeUseCaseImpl) bind RemoveTasksFromScopeUseCase::class
    factoryOf(::UpdateTasksScopeUseCaseImpl) bind UpdateTasksScopeUseCase::class

    // Task priority score
    factoryOf(::UpdateTaskReviewPriorityUseCaseImpl) bind UpdateTaskReviewPriorityUseCase::class
    factoryOf(::GetTaskAbovePriorityScoreUseCaseImpl) bind GetTaskAbovePriorityScoreUseCase::class
    factoryOf(::GetTaskBelowPriorityScoreUseCaseImpl) bind GetTaskBelowPriorityScoreUseCase::class
    factoryOf(::GetPlanPriorityScoreUseCaseImpl) bind GetPlanPriorityScoreUseCase::class
    factoryOf(::GetHighestPriorityScoreUseCaseImpl) bind GetHighestPriorityScoreUseCase::class
    factoryOf(::GetLowestPriorityScoreUseCaseImpl) bind GetLowestPriorityScoreUseCase::class
    factoryOf(::GetDefaultPriorityScoreUseCaseImpl) bind GetDefaultPriorityScoreUseCase::class
    factoryOf(::IncreaseTaskPriorityScoreUseCaseImpl) bind IncrementTaskPriorityScoreUseCase::class
    factoryOf(::DecreaseTaskPriorityScoreUseCaseImpl) bind DecrementTaskPriorityScoreUseCase::class
    factoryOf(::ReorderTaskUseCaseImpl) bind ReorderTaskUseCase::class
    factoryOf(::UpdateTaskPriorityScoreUseCaseImpl) bind UpdateTaskPriorityScoreUseCase::class
    factoryOf(::UpdatePlannedTasksPriorityUseCaseImpl) bind UpdatePlannedTasksPriorityUseCase::class
    factoryOf(::ClearTaskNotificationsUseCaseImpl) bind ClearTaskNotificationsUseCase::class
    factoryOf(::GetReviewTasksUseCaseImpl) bind GetReviewTasksUseCase::class
    factoryOf(::GetTasksByIdsUseCaseImpl) bind GetTasksByIdsUseCase::class
}

val scopeUseCases = module{
    factoryOf(::CreateScopeUseCaseImpl) bind CreateScopeUseCase::class
    factoryOf(::DeleteScopeUseCaseImpl) bind DeleteScopeUseCase::class
    factoryOf(::GetScopesUseCaseImpl) bind GetScopesUseCase::class
    factoryOf(::UpdateScopeNameUseCaseImpl) bind UpdateScopeNameUseCase::class
    factoryOf(::UpdateScopesOrderUseCaseImpl) bind UpdateScopesOrderUseCase::class
    factoryOf(::GetScopesFlowUseCaseImpl) bind GetScopesFlowUseCase::class
    factoryOf(::InitScopesUseCaseImpl) bind InitScopesUseCase::class
    factoryOf(::GetScopeByTaskIdUseCaseImpl) bind GetScopeByTaskIdUseCase::class


}

val scheduleUseCases = module {
    factoryOf(::GetActiveSchedulesUseCaseImpl) bind GetActiveSchedulesUseCase::class
    factoryOf(::CreateScheduleUseCaseImpl) bind CreateScheduleUseCase::class
    factoryOf(::CreateOneTimeScheduleUseCaseImpl) bind CreateOneTimeScheduleUseCase::class
    factoryOf(::CreateReminderUseCaseImpl) bind CreateReminderUseCase::class
    factoryOf(::ToggleQuickReminderUseCaseImpl) bind ToggleQuickReminderUseCase::class
    factoryOf(::GetActiveScheduleFlowUseCaseImpl) bind GetActiveScheduleFlowUseCase::class
    factoryOf(::CancelActiveScheduleUseCaseImpl) bind CancelActiveScheduleUseCase::class
    factoryOf(::GetAllActiveSchedulesUseCaseImpl) bind GetAllActiveSchedulesUseCase::class
    factoryOf(::GetScheduleImpl) bind GetScheduleUseCase::class
    factoryOf(::SetScheduleFulfilledUseCaseImpl) bind SetScheduleFulfilledUseCase::class
    factoryOf(::SetScheduleMessageResponseUseCaseImpl) bind SetScheduleMessageResponseUseCase::class
    factoryOf(::GetTaskScheduleCountUseCaseImpl) bind GetTaskScheduleCountUseCase::class
    factoryOf(::ScheduleAtTimeUseCaseImpl) bind ScheduleAtTimeUseCase::class
    factoryOf(::ScheduleWorkUseCaseImpl) bind ScheduleWorkUseCase::class


}

val messageUseCases = module {
    factoryOf(::GetMessagesUseCaseImpl) bind GetMessagesUseCase::class
    factoryOf(::CreateMessageUseCaseImpl) bind CreateMessageUseCase::class
    factoryOf(::GetMessageImpl) bind GetMessageUseCase::class
    factoryOf(::CreateTaskConfirmationMessageUseCaseImpl) bind CreateTaskConfirmationMessageUseCase::class
    factoryOf(::CreateScheduleMessageUseCaseImpl) bind CreateScheduleMessageUseCase::class
    factoryOf(::ClearActivePendingMessagesUseCaseImpl) bind ClearActiveReminderMessagesUseCase::class
    factoryOf(::CountActiveReminderMessagesUseCaseImpl) bind CountActiveReminderMessagesUseCase::class
    factoryOf(::GetTaskChatMessagesUseCaseImpl) bind GetTaskChatMessagesUseCase::class
    factoryOf(::FetchOpenGraphMetadataUseCaseImpl) bind FetchOpenGraphMetadataUseCase::class
    factoryOf(::CheckForUrlMetadataUseCaseImpl) bind CheckForUrlMetadataUseCase::class
    factoryOf(::SaveMediaUseCaseImpl) bind SaveMediaUseCase::class
    factoryOf(::SaveUserPDFUseCaseImpl) bind SaveUserPDFUseCase::class
    factoryOf(::DeleteMessageUseCaseImpl) bind DeleteMessageUseCase::class
    factoryOf(::CreateMediaMessageUseCaseImpl) bind CreateMediaMessageUseCase::class
    factoryOf(::CreatePDFMessageUseCaseImpl) bind CreatePDFMessageUseCase::class
    factoryOf(::GetLastMessageFlowUseCaseImpl) bind GetLastMessageFlowUseCase::class
    factoryOf(::UpdateMessageContentUseCaseImpl) bind UpdateMessageContentUseCase::class

}


val settingsUseCases = module {
    factoryOf(::GetAppSettingsUseCaseImpl) bind GetAppSettingsUseCase::class
    factoryOf(::GetAppSettingUseCaseImpl) bind GetAppSettingUseCase::class
    factoryOf(::SaveUserSettingUseCaseImpl) bind SaveUserSettingUseCase::class
    factoryOf(::GetAppThemeUseCaseImpl) bind GetAppThemeUseCase::class
    factoryOf(::CheckFirstTimeUseCaseImpl) bind CheckFirstTimeUseCase::class
}

val timeManagerModule = module {
    singleOf(::TimeManagerImpl) bind TimeManager::class
}


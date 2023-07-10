package co.softov.morestuff.android.di

import co.softov.morestuff.android.app.features.VoiceToTextParser
import co.softov.morestuff.android.data.service.ClipboardHandlerImpl
import co.softov.morestuff.android.data.service.TimeManagerImpl
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.DevMiddleware
import co.softov.morestuff.android.domain.redux.middleware.ErrorMiddleware
import co.softov.morestuff.android.domain.redux.middleware.LoggerMiddleware
import co.softov.morestuff.android.domain.redux.middleware.MessageMiddleware
import co.softov.morestuff.android.domain.redux.middleware.NotificationMiddleware
import co.softov.morestuff.android.domain.redux.middleware.PriorityMiddleware
import co.softov.morestuff.android.domain.redux.middleware.ReminderMiddleware
import co.softov.morestuff.android.domain.redux.middleware.ScheduleMiddleware
import co.softov.morestuff.android.domain.redux.middleware.SettingsMiddleware
import co.softov.morestuff.android.domain.redux.middleware.TaskMiddleware
import co.softov.morestuff.android.domain.service.ClipboardHandler
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.service.VoiceToTextInterface
import co.softov.morestuff.android.domain.usecase.message.CheckForUrlMetadataUseCase
import co.softov.morestuff.android.domain.usecase.message.CheckForUrlMetadataUseCaseImpl
import co.softov.morestuff.android.domain.usecase.message.ClearActivePendingMessagesUseCaseImpl
import co.softov.morestuff.android.domain.usecase.message.ClearActiveReminderMessagesUseCase
import co.softov.morestuff.android.domain.usecase.message.CountActiveReminderMessagesUseCase
import co.softov.morestuff.android.domain.usecase.message.CountActiveReminderMessagesUseCaseImpl
import co.softov.morestuff.android.domain.usecase.message.CreateMessageUseCase
import co.softov.morestuff.android.domain.usecase.message.CreateMessageUseCaseImpl
import co.softov.morestuff.android.domain.usecase.message.CreateScheduleMessageUseCase
import co.softov.morestuff.android.domain.usecase.message.CreateScheduleMessageUseCaseImpl
import co.softov.morestuff.android.domain.usecase.message.CreateTaskConfirmationMessageUseCase
import co.softov.morestuff.android.domain.usecase.message.CreateTaskConfirmationMessageUseCaseImpl
import co.softov.morestuff.android.domain.usecase.message.DeleteMessageUseCase
import co.softov.morestuff.android.domain.usecase.message.DeleteMessageUseCaseImpl
import co.softov.morestuff.android.domain.usecase.message.FetchOpenGraphMetadataUseCase
import co.softov.morestuff.android.domain.usecase.message.FetchOpenGraphMetadataUseCaseImpl
import co.softov.morestuff.android.domain.usecase.message.GetActiveMessagesImpl
import co.softov.morestuff.android.domain.usecase.message.GetActiveScheduleMessages
import co.softov.morestuff.android.domain.usecase.message.GetMessageImpl
import co.softov.morestuff.android.domain.usecase.message.GetMessageUseCase
import co.softov.morestuff.android.domain.usecase.message.GetMessagesUseCase
import co.softov.morestuff.android.domain.usecase.message.GetMessagesUseCaseImpl
import co.softov.morestuff.android.domain.usecase.message.GetTaskChatMessagesUseCase
import co.softov.morestuff.android.domain.usecase.message.GetTaskChatMessagesUseCaseImpl
import co.softov.morestuff.android.domain.usecase.message.GetTaskMessagesFlowUseCase
import co.softov.morestuff.android.domain.usecase.message.GetTaskMessagesFlowUseCaseImpl
import co.softov.morestuff.android.domain.usecase.message.SetScheduleMessageResponseUseCase
import co.softov.morestuff.android.domain.usecase.message.SetScheduleMessageResponseUseCaseImpl
import co.softov.morestuff.android.domain.usecase.priority.GetReviewSchedulesUseCase
import co.softov.morestuff.android.domain.usecase.priority.GetReviewSchedulesUseCaseImpl
import co.softov.morestuff.android.domain.usecase.schedule.BootCompleteSchedulerUseCase
import co.softov.morestuff.android.domain.usecase.schedule.BootCompleteSchedulerUseCaseImpl
import co.softov.morestuff.android.domain.usecase.schedule.CancelActiveScheduleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.CancelActiveScheduleUseCaseImpl
import co.softov.morestuff.android.domain.usecase.schedule.CreateOneTimeScheduleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.CreateOneTimeScheduleUseCaseImpl
import co.softov.morestuff.android.domain.usecase.schedule.CreateReminderScheduleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.CreateReminderScheduleUseCaseImpl
import co.softov.morestuff.android.domain.usecase.schedule.CreateScheduleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.CreateScheduleUseCaseImpl
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveScheduleFlowUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveScheduleFlowUseCaseImpl
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveScheduleForTaskUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveScheduleForTaskUseCaseImpl
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveScheduleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveScheduleUseCaseImpl
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveSchedulesByPriority
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveSchedulesByPriorityImpl
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveSchedulesUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveSchedulesUseCaseImpl
import co.softov.morestuff.android.domain.usecase.schedule.GetLaterSchedulesWithTitleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetLaterSchedulesWithTitleUseCaseImpl
import co.softov.morestuff.android.domain.usecase.schedule.GetScheduleImpl
import co.softov.morestuff.android.domain.usecase.schedule.GetScheduleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetScheduleWithTitleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetScheduleWithTitleUseCaseImpl
import co.softov.morestuff.android.domain.usecase.schedule.GetSchedulesWithTitleFlowUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetSchedulesWithTitleImplUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetSchedulesWithTitleListImpl
import co.softov.morestuff.android.domain.usecase.schedule.GetSchedulesWithTitleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetTaskScheduleCountUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetTaskScheduleCountUseCaseImpl
import co.softov.morestuff.android.domain.usecase.schedule.GetTodaySchedulesWithTitleFlowUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetTodaySchedulesWithTitleFlowUseCaseImpl
import co.softov.morestuff.android.domain.usecase.schedule.GetTodaySchedulesWithTitleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetTodaySchedulesWithTitleUseCaseImpl
import co.softov.morestuff.android.domain.usecase.schedule.GetTomorrowSchedulesWithTitleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetTomorrowSchedulesWithTitleUseCaseImpl
import co.softov.morestuff.android.domain.usecase.schedule.ScheduleAtTimeUseCase
import co.softov.morestuff.android.domain.usecase.schedule.ScheduleAtTimeUseCaseImpl
import co.softov.morestuff.android.domain.usecase.schedule.ScheduleNextReviewNotificationsUseCase
import co.softov.morestuff.android.domain.usecase.schedule.ScheduleNextReviewNotificationsUseCaseImpl
import co.softov.morestuff.android.domain.usecase.schedule.SetScheduleFulfilledUseCase
import co.softov.morestuff.android.domain.usecase.schedule.SetScheduleFulfilledUseCaseImpl
import co.softov.morestuff.android.domain.usecase.settings.CheckFirstTimeImplUseCase
import co.softov.morestuff.android.domain.usecase.settings.CheckFirstTimeUseCase
import co.softov.morestuff.android.domain.usecase.settings.GetUserSettingsUseCase
import co.softov.morestuff.android.domain.usecase.settings.GetUserSettingsUseCaseImpl
import co.softov.morestuff.android.domain.usecase.settings.SaveUserSettingsUseCase
import co.softov.morestuff.android.domain.usecase.settings.SaveUserSettingsUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.CreateNewTaskUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.CreateTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.DecreaseTaskPriorityScoreUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.DecrementTaskPriorityScoreUseCase
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksUseCase
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksWithScheduleUseCase
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksWithScheduleUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.GetCompletedTasksUseCase
import co.softov.morestuff.android.domain.usecase.task.GetCompletedTasksUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.GetDefaultPriorityScoreUseCase
import co.softov.morestuff.android.domain.usecase.task.GetDefaultPriorityScoreUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.GetHighestPriorityScoreUseCase
import co.softov.morestuff.android.domain.usecase.task.GetHighestPriorityScoreUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.GetLaterTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.GetLaterTaskUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.GetLowestPriorityScoreUseCase
import co.softov.morestuff.android.domain.usecase.task.GetLowestPriorityScoreUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.GetNowTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.GetNowTaskUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.GetPlanPriorityScoreUseCase
import co.softov.morestuff.android.domain.usecase.task.GetPlanPriorityScoreUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.GetTaskFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.GetTaskFlowUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.GetTaskForScheduleUseCase
import co.softov.morestuff.android.domain.usecase.task.GetTaskForScheduleUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.GetTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.GetTaskUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.GetTasksWithoutScheduleUseCase
import co.softov.morestuff.android.domain.usecase.task.GetTasksWithoutScheduleUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.IncreaseTaskPriorityScoreUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.IncrementTaskPriorityScoreUseCase
import co.softov.morestuff.android.domain.usecase.task.ReorderTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.ReorderTaskUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.SetTasksCompleteImpl
import co.softov.morestuff.android.domain.usecase.task.SetTasksCompleteUseCase
import co.softov.morestuff.android.domain.usecase.task.UpdatePlannedTasksPriorityUseCase
import co.softov.morestuff.android.domain.usecase.task.UpdatePlannedTasksPriorityUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.UpdateTaskPriorityScoreUseCase
import co.softov.morestuff.android.domain.usecase.task.UpdateTaskPriorityScoreUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.UpdateTaskTitleUseCase
import co.softov.morestuff.android.domain.usecase.task.UpdateTaskTitleUseCaseImpl
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import co.softov.morestuff.android.domain.usecase.time.TimeFormatterImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModules
    get() = buildList {
        add(serviceModule) // TODO: is this still being used?
        add(storeModule)
        addAll(useCaseModules)
        add(timeManagerModule)
        add(timeFormatterModule)
    }

val useCaseModules
    get() = buildList {
        add(taskUseCases)
        add(scheduleUseCases)
        add(messageUseCases)
        add(settingsUseCases)
    }

val serviceModule = module {
    factoryOf(::BootCompleteSchedulerUseCaseImpl) bind BootCompleteSchedulerUseCase::class
}

val featuresModule = module {
    factoryOf(::VoiceToTextParser) bind VoiceToTextInterface::class
}


val storeModule = module {
    // Store

    single {
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
            priorityMiddleware = get()
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
    factoryOf(::PriorityMiddleware)
    factoryOf(::DevMiddleware)

}

val taskUseCases = module {
    factoryOf(::CreateNewTaskUseCaseImpl) bind CreateTaskUseCase::class
    factoryOf(::GetTaskUseCaseImpl) bind GetTaskUseCase::class
    factoryOf(::GetTaskFlowUseCaseImpl) bind GetTaskFlowUseCase::class
    factoryOf(::GetTaskMessagesFlowUseCaseImpl) bind GetTaskMessagesFlowUseCase::class
    factoryOf(::GetActiveTasksUseCaseImpl) bind GetActiveTasksUseCase::class
    factoryOf(::GetCompletedTasksUseCaseImpl) bind GetCompletedTasksUseCase::class
    factoryOf(::SetTasksCompleteImpl) bind SetTasksCompleteUseCase::class
    factoryOf(::UpdateTaskTitleUseCaseImpl) bind UpdateTaskTitleUseCase::class
    factoryOf(::GetTaskForScheduleUseCaseImpl) bind GetTaskForScheduleUseCase::class
    factoryOf(::GetTasksWithoutScheduleUseCaseImpl) bind GetTasksWithoutScheduleUseCase::class
    factoryOf(::GetActiveTasksWithScheduleUseCaseImpl) bind GetActiveTasksWithScheduleUseCase::class

    // Task priority score
    factoryOf(::GetPlanPriorityScoreUseCaseImpl) bind GetPlanPriorityScoreUseCase::class
    factoryOf(::GetHighestPriorityScoreUseCaseImpl) bind GetHighestPriorityScoreUseCase::class
    factoryOf(::GetLowestPriorityScoreUseCaseImpl) bind GetLowestPriorityScoreUseCase::class
    factoryOf(::GetDefaultPriorityScoreUseCaseImpl) bind GetDefaultPriorityScoreUseCase::class
    factoryOf(::IncreaseTaskPriorityScoreUseCaseImpl) bind IncrementTaskPriorityScoreUseCase::class
    factoryOf(::DecreaseTaskPriorityScoreUseCaseImpl) bind DecrementTaskPriorityScoreUseCase::class
    factoryOf(::GetLaterTaskUseCaseImpl) bind GetLaterTaskUseCase::class
    factoryOf(::GetNowTaskUseCaseImpl) bind GetNowTaskUseCase::class
    factoryOf(::ReorderTaskUseCaseImpl) bind ReorderTaskUseCase::class
    factoryOf(::UpdateTaskPriorityScoreUseCaseImpl) bind UpdateTaskPriorityScoreUseCase::class
    factoryOf(::UpdatePlannedTasksPriorityUseCaseImpl) bind UpdatePlannedTasksPriorityUseCase::class
}


val scheduleUseCases = module {
    factoryOf(::GetActiveScheduleUseCaseImpl) bind GetActiveScheduleUseCase::class
    factoryOf(::CreateScheduleUseCaseImpl) bind CreateScheduleUseCase::class
    factoryOf(::CreateOneTimeScheduleUseCaseImpl) bind CreateOneTimeScheduleUseCase::class
    factoryOf(::CreateReminderScheduleUseCaseImpl) bind CreateReminderScheduleUseCase::class
    factoryOf(::GetActiveScheduleFlowUseCaseImpl) bind GetActiveScheduleFlowUseCase::class
    factoryOf(::CancelActiveScheduleUseCaseImpl) bind CancelActiveScheduleUseCase::class
    factoryOf(::GetScheduleWithTitleUseCaseImpl) bind GetScheduleWithTitleUseCase::class
    factoryOf(::GetSchedulesWithTitleImplUseCase) bind GetSchedulesWithTitleFlowUseCase::class
    factoryOf(::GetLaterSchedulesWithTitleUseCaseImpl) bind GetLaterSchedulesWithTitleUseCase::class
    factoryOf(::GetTodaySchedulesWithTitleUseCaseImpl) bind GetTodaySchedulesWithTitleUseCase::class
    factoryOf(::GetTodaySchedulesWithTitleFlowUseCaseImpl) bind GetTodaySchedulesWithTitleFlowUseCase::class
    factoryOf(::GetTomorrowSchedulesWithTitleUseCaseImpl) bind GetTomorrowSchedulesWithTitleUseCase::class
    factoryOf(::GetSchedulesWithTitleListImpl) bind GetSchedulesWithTitleUseCase::class
    factoryOf(::GetActiveSchedulesUseCaseImpl) bind GetActiveSchedulesUseCase::class
    factoryOf(::GetActiveSchedulesByPriorityImpl) bind GetActiveSchedulesByPriority::class
    factoryOf(::GetScheduleImpl) bind GetScheduleUseCase::class
    factoryOf(::SetScheduleFulfilledUseCaseImpl) bind SetScheduleFulfilledUseCase::class
    factoryOf(::SetScheduleMessageResponseUseCaseImpl) bind SetScheduleMessageResponseUseCase::class
    factoryOf(::GetTaskScheduleCountUseCaseImpl) bind GetTaskScheduleCountUseCase::class
    factoryOf(::ScheduleAtTimeUseCaseImpl) bind ScheduleAtTimeUseCase::class
    factoryOf(::GetReviewSchedulesUseCaseImpl) bind GetReviewSchedulesUseCase::class
    factoryOf(::ScheduleNextReviewNotificationsUseCaseImpl) bind ScheduleNextReviewNotificationsUseCase::class
    factoryOf(::GetActiveScheduleForTaskUseCaseImpl) bind GetActiveScheduleForTaskUseCase::class
}

val messageUseCases = module {
    factoryOf(::GetMessagesUseCaseImpl) bind GetMessagesUseCase::class
    factoryOf(::GetActiveMessagesImpl) bind GetActiveScheduleMessages::class
    factoryOf(::CreateMessageUseCaseImpl) bind CreateMessageUseCase::class
    factoryOf(::GetMessageImpl) bind GetMessageUseCase::class
    factoryOf(::CreateTaskConfirmationMessageUseCaseImpl) bind CreateTaskConfirmationMessageUseCase::class
    factoryOf(::CreateScheduleMessageUseCaseImpl) bind CreateScheduleMessageUseCase::class
    factoryOf(::ClearActivePendingMessagesUseCaseImpl) bind ClearActiveReminderMessagesUseCase::class
    factoryOf(::CountActiveReminderMessagesUseCaseImpl) bind CountActiveReminderMessagesUseCase::class
    factoryOf(::GetTaskChatMessagesUseCaseImpl) bind GetTaskChatMessagesUseCase::class
    factoryOf(::FetchOpenGraphMetadataUseCaseImpl) bind FetchOpenGraphMetadataUseCase::class

    factoryOf(::CheckForUrlMetadataUseCaseImpl) bind CheckForUrlMetadataUseCase::class
    factoryOf(::ClipboardHandlerImpl) bind ClipboardHandler::class
    factoryOf(::DeleteMessageUseCaseImpl) bind DeleteMessageUseCase::class
}


val settingsUseCases = module {
    factoryOf(::GetUserSettingsUseCaseImpl) bind GetUserSettingsUseCase::class
    factoryOf(::SaveUserSettingsUseCaseImpl) bind SaveUserSettingsUseCase::class
    factoryOf(::CheckFirstTimeImplUseCase) bind CheckFirstTimeUseCase::class
}

val timeManagerModule = module {
    singleOf(::TimeManagerImpl) bind TimeManager::class
}

val timeFormatterModule = module {
    singleOf(::TimeFormatterImpl) bind TimeFormatter::class
}

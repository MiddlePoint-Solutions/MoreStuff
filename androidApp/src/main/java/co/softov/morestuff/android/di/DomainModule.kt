package co.softov.morestuff.android.di

import co.softov.morestuff.android.domain.service.VoiceToTextInterface
import co.softov.morestuff.android.app.features.VoiceToTextParser
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.*
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.data.service.TimeManagerImpl
import co.softov.morestuff.android.domain.usecase.message.*
import co.softov.morestuff.android.domain.usecase.settings.CheckFirstTimeImplUseCase
import co.softov.morestuff.android.domain.usecase.settings.CheckFirstTimeUseCase
import co.softov.morestuff.android.domain.usecase.priority.*
import co.softov.morestuff.android.domain.usecase.review.GetCurrentReviewNotificationUseCase
import co.softov.morestuff.android.domain.usecase.review.GetCurrentReviewNotificationUseCaseImpl
import co.softov.morestuff.android.domain.usecase.schedule.*
import co.softov.morestuff.android.domain.usecase.settings.GetUserSettingsUseCase
import co.softov.morestuff.android.domain.usecase.settings.GetUserSettingsUseCaseImpl
import co.softov.morestuff.android.domain.usecase.settings.SaveUserSettingsUseCase
import co.softov.morestuff.android.domain.usecase.settings.SaveUserSettingsUseCaseImpl
import co.softov.morestuff.android.domain.usecase.task.*
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
        add(reviewUseCases)
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
            navigator = get(),
            taskMiddleware = get(),
            messageMiddleware = get(),
            scheduleMiddleware = get(),
            responseMiddleware = get(),
            notificationMiddleware = get(),
            settingsMiddleware = get(),
            reviewMiddleware = get()
        )
    }

    // Middleware
    factoryOf(::LoggerMiddleware)
    factoryOf(::NavigationMiddleware)
    factoryOf(::TaskMiddleware)
    factoryOf(::ScheduleMiddleware)
    factoryOf(::MessageMiddleware)
    factoryOf(::NotificationMiddleware)
    factoryOf(::SettingsMiddleware)
    factoryOf(::ReminderMiddleware)
    factoryOf(::ErrorMiddleware)
    factoryOf(::ReviewMiddleware)
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
    factoryOf(::GetTaskForReviewUseCaseImpl) bind GetTaskForReviewUseCase::class
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
    factoryOf(::RescheduleTaskUseCaseImpl) bind RescheduleTaskUseCase::class
    factoryOf(::ScheduleReviewNotificationsUseCaseImpl) bind ScheduleReviewNotificationsUseCase::class
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
}


val settingsUseCases = module {
    factoryOf(::GetUserSettingsUseCaseImpl) bind GetUserSettingsUseCase::class
    factoryOf(::SaveUserSettingsUseCaseImpl) bind SaveUserSettingsUseCase::class
    factoryOf(::CheckFirstTimeImplUseCase) bind CheckFirstTimeUseCase::class
}

val reviewUseCases = module {
    factoryOf(::GetCurrentReviewNotificationUseCaseImpl) bind GetCurrentReviewNotificationUseCase::class
}

val timeManagerModule = module {
    singleOf(::TimeManagerImpl) bind TimeManager::class
}

val timeFormatterModule = module {
    singleOf(::TimeFormatterImpl) bind TimeFormatter::class
}

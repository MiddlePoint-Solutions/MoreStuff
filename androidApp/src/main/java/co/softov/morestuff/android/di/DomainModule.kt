package co.softov.morestuff.android.di

import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.*
import co.softov.morestuff.android.data.service.TimeManager
import co.softov.morestuff.android.data.service.TimeManagerImpl
import co.softov.morestuff.android.domain.usecase.message.*
import co.softov.morestuff.android.domain.usecase.settings.CheckFirstTimeImplUseCase
import co.softov.morestuff.android.domain.usecase.settings.CheckFirstTimeUseCase
import co.softov.morestuff.android.domain.usecase.priority.*
import co.softov.morestuff.android.domain.usecase.schedule.*
import co.softov.morestuff.android.domain.usecase.settings.GetUserSettingsUseCase
import co.softov.morestuff.android.domain.usecase.settings.GetUserSettingsUseCaseImpl
import co.softov.morestuff.android.domain.usecase.settings.SaveUserSettings
import co.softov.morestuff.android.domain.usecase.settings.SaveUserSettingsImpl
import co.softov.morestuff.android.domain.usecase.task.*
import co.softov.morestuff.android.domain.usecase.time.GetPriorityTimeUseCase
import co.softov.morestuff.android.domain.usecase.time.GetPriorityTimeUseCaseImpl
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
        add(priorityUseCases)
        add(messageUseCases)
        add(settingsUseCases)
    }

val serviceModule = module {
    factoryOf(::BootCompleteSchedulerImpl) bind BootCompleteScheduler::class
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
            priorityMiddleware = get(),
            reviewMiddleware = get(),
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
    factoryOf(::PriorityMiddleware)
    factoryOf(::ErrorMiddleware)
    factoryOf(::ReviewMiddleware)
    factoryOf(::DevMiddleware)

}

val taskUseCases = module {
    factoryOf(::CreateNewTaskUseCaseImpl) bind CreateTaskUseCase::class
    factoryOf(::GetTaskUseCaseImpl) bind GetTaskUseCase::class
    factoryOf(::GetTaskFlowUseCaseImpl) bind GetTaskFlowUseCase::class
    factoryOf(::GetTaskMessagesFlowUseCaseImpl) bind GetTaskMessagesFlowUseCase::class
    factoryOf(::GetActiveTasksImpl) bind GetActiveTasks::class
    factoryOf(::GetCompletedTasksImpl) bind GetCompletedTasks::class
    factoryOf(::SetTasksCompleteImpl) bind SetTasksCompleteUseCase::class
    factoryOf(::UpdateTaskTitleUseCaseImpl) bind UpdateTaskTitleUseCase::class
    factoryOf(::GetTaskForScheduleUseCaseImpl) bind GetTaskForScheduleUseCase::class
}


val scheduleUseCases = module {
    factoryOf(::GetActiveScheduleUseCaseImpl) bind GetActiveScheduleUseCase::class
    factoryOf(::GetActiveScheduleFlowUseCaseImpl) bind GetActiveScheduleFlowUseCase::class
    factoryOf(::CancelActiveScheduleUseCaseImpl) bind CancelActiveScheduleUseCase::class
    factoryOf(::GetScheduleWithTitleImpl) bind GetScheduleWithTitle::class
    factoryOf(::GetSchedulesWithTitleImpl) bind GetSchedulesWithTitleFlow::class
    factoryOf(::GetLaterSchedulesWithTitleImpl) bind GetLaterSchedulesWithTitle::class
    factoryOf(::GetTodaySchedulesWithTitleImpl) bind GetTodaySchedulesWithTitle::class
    factoryOf(::GetTomorrowSchedulesWithTitleImpl) bind GetTomorrowSchedulesWithTitle::class
    factoryOf(::GetSchedulesWithTitleListImpl) bind GetSchedulesWithTitleUseCase::class
    factoryOf(::GetActiveSchedulesImpl) bind GetActiveSchedules::class
    factoryOf(::GetActiveSchedulesByPriorityImpl) bind GetActiveSchedulesByPriority::class
    factoryOf(::CreateScheduleUseCaseImpl) bind CreateScheduleUseCase::class
    factoryOf(::GetScheduleImpl) bind GetScheduleUseCase::class
    factoryOf(::SetScheduleFulfilledUseCaseImpl) bind SetScheduleFulfilledUseCase::class
    factoryOf(::SetScheduleMessageResponseImpl) bind SetScheduleMessageResponse::class
    factoryOf(::GetTaskScheduleCountUseCaseImpl) bind GetTaskScheduleCountUseCase::class
    factoryOf(::ScheduleAtTimeUseCaseImpl) bind ScheduleAtTimeUseCase::class
    factoryOf(::GetSchedulesForPriorityReviewUseCaseImpl) bind GetSchedulesForPriorityReviewUseCase::class
    factoryOf(::RescheduleTaskUseCaseImpl) bind RescheduleTaskUseCase::class
    factoryOf(::ScheduleReviewNotificationsImpl) bind ScheduleReviewNotifications::class
}

val messageUseCases = module {
    factoryOf(::GetMessagesImpl) bind GetMessages::class
    factoryOf(::GetActiveMessagesImpl) bind GetActiveScheduleMessages::class
    factoryOf(::CreateMessageUseCaseImpl) bind CreateMessageUseCase::class
    factoryOf(::GetMessageImpl) bind GetMessageUseCase::class
    factoryOf(::CreateTaskMessageUseCaseImpl) bind CreateTaskMessageUseCase::class
    factoryOf(::CreateTaskConfirmationMessageUseCaseImpl) bind CreateTaskConfirmationMessageUseCase::class
    factoryOf(::CreateScheduleMessageUseCaseImpl) bind CreateScheduleMessageUseCase::class
    factoryOf(::ClearActivePendingMessagesImpl) bind ClearActiveReminderMessages::class
    factoryOf(::CountActiveReminderMessagesImpl) bind CountActiveReminderMessages::class
    factoryOf(::GetTaskChatMessagesUseCaseImpl) bind GetTaskChatMessagesUseCase::class
}

val priorityUseCases = module {
    factoryOf(::GetPriorityTimeUseCaseImpl) bind GetPriorityTimeUseCase::class
    factoryOf(::GetPriorityOptionsUseCase)
    factoryOf(::GetSchedulePriorityUseCaseImpl) bind GetSchedulePriorityUseCase::class
    factoryOf(::MapScheduleToPriorityUseCaseImpl) bind MapScheduleToPriorityUseCase::class
    factoryOf(::GetUpcomingPriorityUseCaseImpl) bind GetUpcomingPriorityUseCase::class
}

val settingsUseCases = module {
    factoryOf(::GetUserSettingsUseCaseImpl) bind GetUserSettingsUseCase::class
    factoryOf(::SaveUserSettingsImpl) bind SaveUserSettings::class
    factoryOf(::CheckFirstTimeImplUseCase) bind CheckFirstTimeUseCase::class
}

val timeManagerModule = module {
    singleOf(::TimeManagerImpl) bind TimeManager::class
}

val timeFormatterModule = module {
    singleOf(::TimeFormatterImpl) bind TimeFormatter::class
}

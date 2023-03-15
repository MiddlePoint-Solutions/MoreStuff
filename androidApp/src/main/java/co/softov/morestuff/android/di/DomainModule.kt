package co.softov.morestuff.android.di

import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.*
import co.softov.morestuff.android.domain.usecase.message.*
import co.softov.morestuff.android.domain.usecase.priority.*
import co.softov.morestuff.android.domain.usecase.schedule.*
import co.softov.morestuff.android.domain.usecase.settings.GetUserSettingsUseCase
import co.softov.morestuff.android.domain.usecase.settings.GetUserSettingsUseCaseImpl
import co.softov.morestuff.android.domain.usecase.settings.SaveUserSettings
import co.softov.morestuff.android.domain.usecase.settings.SaveUserSettingsImpl
import co.softov.morestuff.android.domain.usecase.task.*
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModules get() = buildList {
    add(serviceModule) // TODO: is this still being used?
    add(storeModule)
    addAll(useCaseModules)
}

val useCaseModules get() = buildList {
    add(taskUseCases)
    add(scheduleUseCases)
    add(priorityUseCases)
    add(messageUseCases)
    add(settingsUseCases)
}

val serviceModule = module {
    factory<BootCompleteScheduler> {
        BootCompleteSchedulerImpl(scheduler = get(), getActiveSchedules = get())
    }
}

val storeModule = module {
    // Store

    single {
        AppStore(
            logger = get(),
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

    factory { LoggerMiddleware() }
    factory { NavigationMiddleware(router = get()) }
    factory {
        TaskMiddleware(
            createTaskUseCase = get(),
            setTaskCompleteUseCase = get()
        )
    }
    factory {
        ScheduleMiddleware(
            scheduleAtTimeUseCase = get(),
            getScheduleUseCase = get(),
            createScheduleUseCase = get(),
            cancelActiveScheduleUseCase = get(),
            setScheduleFulfilledUseCase = get(),
            getTaskScheduleCountUseCase = get()
        )
    }
    factory {
        MessageMiddleware(
            createTaskConfirmationMessageUseCase = get(),
            createTaskMessageUseCase = get(),
            createScheduleMessageUseCase = get(),
            setScheduleResponseMessage = get()
        )
    }
    factory { NotificationMiddleware(notifier = get()) }



    factoryOf(::SettingsMiddleware)
    factoryOf(::ReminderMiddleware)
    factoryOf(::PriorityMiddleware)
    factoryOf(::ErrorMiddleware)
    factoryOf(::ReviewMiddleware)

}

val taskUseCases = module {
    factory<CreateTaskUseCase> {
        CreateNewTaskUseCaseImpl(taskRepository = get())
    }

    factory<GetTaskUseCase> { GetTaskUseCaseImpl(taskRepository = get()) }
    factoryOf(::GetTaskFlowUseCaseImpl) bind GetTaskFlowUseCase::class

    factory<GetTaskMessagesFlowUseCase> { GetTaskMessagesFlowUseCaseImpl(messageRepository = get()) }
    factory<GetActiveTasks> { GetActiveTasksImpl(taskRepository = get()) }
    factory<GetCompletedTasks> { GetCompletedTasksImpl(taskRepository = get()) }

    factory<SetTaskCompleteUseCase> {
        SetTaskCompleteImpl(
            taskRepository = get()
        )
    }

    factoryOf(::UpdateTaskTitleUseCaseImpl) bind UpdateTaskTitleUseCase::class
    factoryOf(::GetTaskForScheduleUseCaseImpl) bind GetTaskForScheduleUseCase::class
}


val scheduleUseCases = module {
    factory<GetActiveScheduleUseCase> { GetActiveScheduleUseCaseImpl(scheduleRepository = get()) }
    factoryOf(::GetActiveScheduleFlowUseCaseImpl) bind GetActiveScheduleFlowUseCase::class

    factory<CancelActiveScheduleUseCase> {
        CancelActiveScheduleUseCaseImpl(
            scheduler = get(),
            getActiveSchedule = get(),
            setScheduleFulfilled = get()
        )
    }

    factory<GetScheduleWithTitle> { GetScheduleWithTitleImpl(scheduleRepository = get()) }
    factory<GetSchedulesWithTitleFlow> { GetSchedulesWithTitleImpl(scheduleRepository = get()) }
    factory<GetLaterSchedulesWithTitle> { GetLaterSchedulesWithTitleImpl(scheduleRepository = get()) }
    factory<GetTodaySchedulesWithTitle> { GetTodaySchedulesWithTitleImpl(scheduleRepository = get()) }
    factory<GetTomorrowSchedulesWithTitle> { GetTomorrowSchedulesWithTitleImpl(scheduleRepository = get()) }
    factory<GetSchedulesWithTitleList> { GetSchedulesWithTitleListImpl(scheduleRepository = get()) }

    factory<GetActiveSchedules> {
        GetActiveSchedulesImpl(scheduleRepository = get())
    }

    factory<GetActiveSchedulesByPriority> {
        GetActiveSchedulesByPriorityImpl(getActiveSchedules = get())
    }

    factory<CreateScheduleUseCase> {
        CreateScheduleUseCaseImpl(
            scheduleRepository = get(),
            timeManager = get()
        )
    }

    factory<GetScheduleUseCase> {
        GetScheduleImpl(scheduleRepository = get())
    }
    factory<SetScheduleFulfilledUseCase> {
        SetScheduleFulfilledUseCaseImpl(scheduleRepository = get())
    }
    factory<SetScheduleMessageResponse> { SetScheduleMessageResponseImpl(messageRepository = get()) }

    factory<GetTaskScheduleCountUseCase> {
        GetTaskScheduleCountUseCaseImpl(
            scheduleRepository = get(),
            timeManager = get()
        )
    }

    factory<ScheduleAtTimeUseCase> {
        ScheduleAtTimeUseCaseImpl(scheduler = get())
    }
}

val messageUseCases = module {
    factoryOf(::GetMessagesImpl) bind GetMessages::class

    factory<GetActiveScheduleMessages> { GetActiveMessagesImpl(messageRepository = get()) }
    factory<CreateMessageUseCase> { CreateMessageUseCaseImpl(messageRepository = get()) }
    factory<GetMessageUseCase> { GetMessageImpl(messageRepository = get()) }
    factory<CreateTaskMessageUseCase> {
        CreateTaskMessageUseCaseImpl(createMessageUseCase = get())
    }
    factory<CreateTaskConfirmationMessageUseCase> {
        CreateTaskConfirmationMessageUseCaseImpl(createMessageUseCase = get())
    }
    factory<CreateScheduleMessageUseCase> {
        CreateScheduleMessageUseCaseImpl(
            createMessageUseCase = get(),
            getTaskForScheduleUseCase = get()
        )
    }
}

val priorityUseCases = module {
    factoryOf(::GetPriorityOptionsUseCase)
    factoryOf(::GetSchedulePriorityUseCaseImpl) bind GetSchedulePriorityUseCase::class
    factoryOf(::MapScheduleToPriorityUseCaseImpl) bind MapScheduleToPriorityUseCase::class
    factoryOf(::GetUpcomingPriorityUseCaseImpl) bind GetUpcomingPriorityUseCase::class
}

val settingsUseCases = module {
    factoryOf(::GetUserSettingsUseCaseImpl) bind GetUserSettingsUseCase::class
    factoryOf(::SaveUserSettingsImpl) bind SaveUserSettings::class
}



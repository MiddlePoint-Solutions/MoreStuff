package co.softov.morestuff.android.di

import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.*
import co.softov.morestuff.android.domain.usecase.message.*
import co.softov.morestuff.android.domain.usecase.priority.GetPriorityOptionsUseCase
import co.softov.morestuff.android.domain.usecase.schedule.*
import co.softov.morestuff.android.domain.usecase.task.*
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

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
            userMiddleware = get(),
            priorityMiddleware = get(),
            reviewMiddleware = get()
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
    factory { UserMiddleware(userRepository = get()) }
    factoryOf(::ReminderMiddleware)
    factoryOf(::PriorityMiddleware)
    factoryOf(::ErrorMiddleware)
    factoryOf(::ReviewMiddleware)

}

val TaskUseCases = module {
    factory<CreateTaskUseCase> {
        CreateNewTaskUseCaseImpl(taskRepository = get())
    }

    factory<GetTaskUseCase> { GetTaskUseCaseImpl(taskRepository = get()) }
    factory<GetMessagesForTask> { GetMessagesForTaskImpl(messageRepository = get()) }
    factory<GetActiveTasks> { GetActiveTasksImpl(taskRepository = get()) }
    factory<GetCompletedTasks> { GetCompletedTasksImpl(taskRepository = get()) }

    factory<SetTaskCompleteUseCase> {
        SetTaskCompleteImpl(
            taskRepository = get()
        )
    }

    factory<GetScheduleTaskUseCase> {
        GetScheduleTaskUseCaseImpl(
            getScheduleUseCase = get(),
            getTaskUseCase = get()
        )
    }
}

val scheduleUseCases = module {
    factory<GetActiveSchedule> { GetActiveScheduleImpl(scheduleRepository = get()) }
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
    factory<SetScheduleResponseMessage> { AddReminderReplyMessageImpl(messageRepository = get()) }

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
    factory<GetMessages> { GetMessagesImpl(messageRepository = get()) }
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
            getScheduleTaskUseCase = get()
        )
    }
}

val priorityUseCases = module {
    factoryOf(::GetPriorityOptionsUseCase)
}
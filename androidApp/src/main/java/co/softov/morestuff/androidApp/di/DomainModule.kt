package co.softov.morestuff.androidApp.di

import co.softov.morestuff.androidApp.domain.redux.AppStore
import co.softov.morestuff.androidApp.domain.redux.middleware.*
import co.softov.morestuff.androidApp.domain.redux.state.UserMiddleware
import co.softov.morestuff.androidApp.domain.usecase.message.*
import co.softov.morestuff.androidApp.domain.usecase.schedule.*
import co.softov.morestuff.androidApp.domain.usecase.task.*
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
            navigator = get(),
            taskMiddleware = get(),
            messageMiddleware = get(),
            scheduleMiddleware = get(),
            responseMiddleware = get(),
            notificationMiddleware = get(),
            userMiddleware = get()
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
            scheduler = get(),
            getScheduleUseCase = get(),
            createScheduleUseCase = get(),
            cancelActiveScheduleUseCase = get(),
            setScheduleFulfilledUseCase = get(),
            countTaskSchedulesUseCase = get()
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
    factory { ResponseMiddleware(getScheduleUseCase = get()) }
    factory { NotificationMiddleware(notifier = get()) }
    factory { UserMiddleware(userRepository = get()) }
}

val taskUseCases = module {
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
            getActiveSchedule = get(),
            setScheduleFulfilled = get()
        )
    }

    factory<GetScheduleWithTitle> { GetScheduleWithTitleImpl(scheduleRepository = get()) }
    factory<GetSchedulesWithTitle> { GetSchedulesWithTitleImpl(scheduleRepository = get()) }
    factory<GetLaterSchedulesWithTitle> { GetLaterSchedulesWithTitleImpl(scheduleRepository = get()) }
    factory<GetTodaySchedulesWithTitle> { GetTodaySchedulesWithTitleImpl(scheduleRepository = get()) }
    factory<GetTomorrowSchedulesWithTitle> { GetTomorrowSchedulesWithTitleImpl(scheduleRepository = get()) }

    factory<GetActiveSchedules> {
        GetActiveSchedulesImpl(scheduleRepository = get())
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

    factory<CountTaskSchedulesUseCase> {
        CountTaskSchedulesUseCaseImpl(
            scheduleRepository = get(),
            timeManager = get()
        )
    }
}

val messageUseCases = module {
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
package co.softov.morestuff.androidApp.di

import co.softov.morestuff.androidApp.domain.redux.AppStore
import co.softov.morestuff.androidApp.domain.redux.middleware.*
import co.softov.morestuff.androidApp.domain.usecase.message.*
import co.softov.morestuff.androidApp.domain.usecase.schedule.*
import co.softov.morestuff.androidApp.domain.usecase.task.*
import org.koin.dsl.module

val domainModule = module {

    // Redux Store
    single {
        AppStore(
            logger = get(),
            navigator = get(),
            taskMiddleware = get(),
            messageMiddleware = get(),
            scheduleMiddleware = get(),
            responseMiddleware = get()
        )
    }

    // Redux middleware
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
            createScheduleUseCase = get(),
            cancelActiveScheduleUseCase = get(),
            rescheduleUseCase = get(),
            scheduler = get(),
            setScheduleFulfilledUseCase = get()
        )
    }
    factory {
        MessageMiddleware(
            notifier = get(),
            createTaskConfirmationMessageUseCase = get(),
            createTaskMessageUseCase = get(),
            createScheduleMessageUseCase = get()
        )
    }
    factory {
        ResponseMiddleware(
            handleScheduleResponseUseCase = get()
        )
    }

    // UseCase
    factory<CreateTaskUseCase> {
        CreateNewTaskUseCaseImpl(taskRepository = get())
    }

    factory<BootCompleteScheduler> {
        BootCompleteSchedulerImpl(scheduler = get(), getActiveSchedules = get())
    }
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
    factory<HandleScheduleResponseUseCase> {
        HandleScheduleResponseUseCaseImpl(
            notifier = get(),
            getSchedule = get(),
            createSchedule = get(),
            setScheduleResponseMessage = get(),
            setTaskComplete = get()
        )
    }

    factory<SetScheduleResponseMessage> { AddReminderReplyMessageImpl(messageRepository = get()) }

    // Tasks use-cases
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

    // Schedule use-cases
    factory<GetActiveSchedule> { GetActiveScheduleImpl(scheduleRepository = get()) }
    factory<RescheduleUseCase> {
        RescheduleUseCaseImpl(
            cancelActiveSchedule = get(),
            createSchedule = get()
        )
    }
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

    // Message use-cases
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
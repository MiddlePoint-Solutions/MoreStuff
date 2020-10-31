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
            scheduleMiddleware = get()
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
            rescheduleUseCase = get()
        )
    }
    factory {
        MessageMiddleware(
            createTaskConfirmationMessage = get(),
            createTaskMessageUseCase = get()
        )
    }

    // UseCase
    factory<CreateTaskUseCase> {
        CreateNewTaskUseCaseImpl(taskRepository = get())
    }

    factory<ExecuteSchedule> {
        ExecuteScheduleImpl(
            getScheduleWithTitle = get(),
            messageRepository = get(),
            notifier = get(),
            setScheduleFulfilled = get()
        )
    }

    factory<BootCompleteScheduler> {
        BootCompleteSchedulerImpl(scheduler = get(), getActiveSchedules = get())
    }
    factory<GetActiveSchedules> {
        GetActiveSchedulesImpl(scheduleRepository = get())
    }
    factory<CreateTaskMessageUseCase> {
        CreateTaskMessageUseCaseImpl(messageRepository = get())
    }
    factory<CreateTaskConfirmationMessageUseCase> {
        CreateTaskConfirmationMessageUseCaseImpl(messageRepository = get())
    }
    factory<CreateScheduleUseCase> {
        CreateScheduleUseCaseImpl(
            scheduleRepository = get(),
            timeManager = get(),
            scheduler = get()
        )
    }
    factory<GetMessage> {
        GetMessageImpl(messageRepository = get())
    }
    factory<GetSchedule> {
        GetScheduleImpl(scheduleRepository = get())
    }
    factory<SetScheduleFulfilled> {
        SetScheduleFulfilledImpl(scheduleRepository = get())
    }
    factory<HandleScheduleResponse> {
        HandleScheduleResponseImpl(
            notifier = get(),
            getSchedule = get(),
            createSchedule = get(),
            setScheduleResponseMessage = get(),
            getTaskMessages = get(),
            setTaskComplete = get()
        )
    }

    factory<SetScheduleResponseMessage> { AddReminderReplyMessageImpl(messageRepository = get()) }

    // Tasks use-cases
    factory<GetTask> { GetTaskImpl(taskRepository = get()) }
    factory<GetMessagesForTask> { GetMessagesForTaskImpl(messageRepository = get()) }
    factory<GetActiveTasks> { GetActiveTasksImpl(taskRepository = get()) }
    factory<GetCompletedTasks> { GetCompletedTasksImpl(taskRepository = get()) }

    factory<SetTaskCompleteUseCase> {
        SetTaskCompleteImpl(
            taskRepository = get()
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
            setScheduleFulfilled = get(),
            scheduler = get()
        )
    }

    factory<GetScheduleWithTitle> { GetScheduleWithTitleImpl(scheduleRepository = get()) }
    factory<GetSchedulesWithTitle> { GetSchedulesWithTitleImpl(scheduleRepository = get()) }
    factory<GetLaterSchedulesWithTitle> { GetLaterSchedulesWithTitleImpl(scheduleRepository = get()) }
    factory<GetTodaySchedulesWithTitle> { GetTodaySchedulesWithTitleImpl(scheduleRepository = get()) }
    factory<GetTomorrowSchedulesWithTitle> { GetTomorrowSchedulesWithTitleImpl(scheduleRepository = get()) }
}
package co.softov.morestuff.androidApp.di

import co.softov.morestuff.androidApp.domain.redux.AppStore
import co.softov.morestuff.androidApp.domain.redux.middleware.LoggerMiddleware
import co.softov.morestuff.androidApp.domain.redux.middleware.NavigationMiddleware
import co.softov.morestuff.androidApp.domain.usecase.message.*
import co.softov.morestuff.androidApp.domain.usecase.schedule.*
import co.softov.morestuff.androidApp.domain.usecase.task.*
import org.koin.dsl.module

val domainModule = module {

    // Redux Store
    single {
        AppStore(
            logger = get(),
            navigator = get()
        )
    }

    // Redux middleware
    factory { LoggerMiddleware() }
    factory { NavigationMiddleware(router = get()) }

    // UseCase
    factory<CreateTask> {
        CreateNewTaskImpl(
            taskRepository = get(),
            createTaskMessage = get(),
            createConfirmationMessage = get(),
            createSchedule = get()
        )
    }

    factory<ExecuteSchedule> {
        ExecuteScheduleImpl(
            getSchedule = get(),
            getTask = get(),
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
    factory<CreateTaskMessage> {
        CreateTaskMessageImpl(messageRepository = get())
    }
    factory<CreateTaskConfirmationMessage> {
        CreateConfirmationMessageImpl(messageRepository = get())
    }
    factory<CreateSchedule> {
        CreateScheduleImpl(scheduleRepository = get(), timeManager = get(), scheduler = get())
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
    factory<GetSchedulesWithTitle> { GetSchedulesWithTitleImpl(scheduleRepository = get()) }
    factory<GetLaterSchedulesWithTitle> { GetLaterSchedulesWithTitleImpl(scheduleRepository = get()) }
    factory<GetTodaySchedulesWithTitle> { GetTodaySchedulesWithTitleImpl(scheduleRepository = get()) }
    factory<GetTomorrowSchedulesWithTitle> { GetTomorrowSchedulesWithTitleImpl(scheduleRepository = get()) }

    factory<SetTaskComplete> {
        SetTaskCompleteImpl(
            taskRepository = get(),
            cancelActiveTaskSchedule = get()
        )
    }

    // Schedule use-cases
    factory<GetActiveSchedule> { GetActiveScheduleImpl(scheduleRepository = get()) }
    factory<RescheduleTask> {
        RescheduleTaskImpl(
            cancelActiveSchedule = get(),
            createSchedule = get()
        )
    }
    factory<CancelActiveScheduleForTask> {
        CancelActiveScheduleForTaskImpl(
            getActiveSchedule = get(),
            setScheduleFulfilled = get(),
            scheduler = get()
        )
    }
}
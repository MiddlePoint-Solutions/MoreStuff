package co.softov.morestuff.androidApp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import co.softov.morestuff.androidApp.app.Debugger
import co.softov.morestuff.androidApp.app.SchedulerImpl
import co.softov.morestuff.androidApp.data.mapper.makeMessageDbMapper
import co.softov.morestuff.androidApp.data.mapper.makeScheduleDbMapper
import co.softov.morestuff.androidApp.data.mapper.makeScheduleWithTitleDbMapper
import co.softov.morestuff.androidApp.data.mapper.makeTaskDbMapper
import co.softov.morestuff.androidApp.data.repository.MessageRepositoryImpl
import co.softov.morestuff.androidApp.data.repository.PreferenceRepositoryImpl
import co.softov.morestuff.androidApp.data.repository.ScheduleRepositoryImpl
import co.softov.morestuff.androidApp.data.repository.TaskRepositoryImpl
import co.softov.morestuff.androidApp.data.service.NotifierImpl
import co.softov.morestuff.db.Message
import co.softov.morestuff.db.Schedule
import co.softov.morestuff.androidApp.domain.Debug
import co.softov.morestuff.androidApp.domain.Scheduler
import co.softov.morestuff.androidApp.domain.repository.MessageRepository
import co.softov.morestuff.androidApp.domain.repository.PreferenceRepository
import co.softov.morestuff.androidApp.domain.repository.ScheduleRepository
import co.softov.morestuff.androidApp.domain.repository.TaskRepository
import co.softov.morestuff.androidApp.domain.service.Notifier
import co.softov.morestuff.androidApp.domain.usecase.message.AddReminderReplyMessageImpl
import co.softov.morestuff.androidApp.domain.usecase.message.CreateConfirmationMessageImpl
import co.softov.morestuff.androidApp.domain.usecase.message.CreateTaskConfirmationMessage
import co.softov.morestuff.androidApp.domain.usecase.message.CreateTaskMessage
import co.softov.morestuff.androidApp.domain.usecase.message.CreateTaskMessageImpl
import co.softov.morestuff.androidApp.domain.usecase.message.GetMessage
import co.softov.morestuff.androidApp.domain.usecase.message.GetMessageImpl
import co.softov.morestuff.androidApp.domain.usecase.message.GetMessagesForTask
import co.softov.morestuff.androidApp.domain.usecase.message.GetMessagesForTaskImpl
import co.softov.morestuff.androidApp.domain.usecase.message.GetPagedMessages
import co.softov.morestuff.androidApp.domain.usecase.message.GetPagedMessagesImpl
import co.softov.morestuff.androidApp.domain.usecase.message.SetScheduleResponseMessage
import co.softov.morestuff.androidApp.domain.usecase.schedule.BootCompleteScheduler
import co.softov.morestuff.androidApp.domain.usecase.schedule.BootCompleteSchedulerImpl
import co.softov.morestuff.androidApp.domain.usecase.schedule.CancelActiveScheduleForTask
import co.softov.morestuff.androidApp.domain.usecase.schedule.CancelActiveScheduleForTaskImpl
import co.softov.morestuff.androidApp.domain.usecase.schedule.CreateSchedule
import co.softov.morestuff.androidApp.domain.usecase.schedule.CreateScheduleImpl
import co.softov.morestuff.androidApp.domain.usecase.schedule.ExecuteSchedule
import co.softov.morestuff.androidApp.domain.usecase.schedule.ExecuteScheduleImpl
import co.softov.morestuff.androidApp.domain.usecase.schedule.GetActiveSchedule
import co.softov.morestuff.androidApp.domain.usecase.schedule.GetActiveScheduleImpl
import co.softov.morestuff.androidApp.domain.usecase.schedule.GetActiveSchedules
import co.softov.morestuff.androidApp.domain.usecase.schedule.GetActiveSchedulesImpl
import co.softov.morestuff.androidApp.domain.usecase.schedule.GetSchedule
import co.softov.morestuff.androidApp.domain.usecase.schedule.GetScheduleImpl
import co.softov.morestuff.androidApp.domain.usecase.schedule.GetSchedulesWithTitle
import co.softov.morestuff.androidApp.domain.usecase.schedule.GetSchedulesWithTitleImpl
import co.softov.morestuff.androidApp.domain.usecase.schedule.HandleScheduleResponse
import co.softov.morestuff.androidApp.domain.usecase.schedule.HandleScheduleResponseImpl
import co.softov.morestuff.androidApp.domain.usecase.schedule.RescheduleTask
import co.softov.morestuff.androidApp.domain.usecase.schedule.RescheduleTaskImpl
import co.softov.morestuff.androidApp.domain.usecase.schedule.SetScheduleFulfilled
import co.softov.morestuff.androidApp.domain.usecase.schedule.SetScheduleFulfilledImpl
import co.softov.morestuff.androidApp.domain.usecase.task.CreateNewTaskImpl
import co.softov.morestuff.androidApp.domain.usecase.task.CreateTask
import co.softov.morestuff.androidApp.domain.usecase.task.GetActiveTasks
import co.softov.morestuff.androidApp.domain.usecase.task.GetActiveTasksImpl
import co.softov.morestuff.androidApp.domain.usecase.task.GetCompletedTasks
import co.softov.morestuff.androidApp.domain.usecase.task.GetCompletedTasksImpl
import co.softov.morestuff.androidApp.domain.usecase.task.GetTask
import co.softov.morestuff.androidApp.domain.usecase.task.GetTaskImpl
import co.softov.morestuff.androidApp.domain.usecase.task.SetTaskComplete
import co.softov.morestuff.androidApp.domain.usecase.task.SetTaskCompleteImpl
import co.softov.morestuff.androidApp.presentation.content.ContentConductor
import co.softov.morestuff.androidApp.presentation.content.ContentViewModel
import co.softov.morestuff.androidApp.presentation.dashboard.DashboardConductor
import co.softov.morestuff.androidApp.presentation.dashboard.DashboardViewModel
import co.softov.morestuff.androidApp.presentation.list.schedule.ScheduleListAdapter
import co.softov.morestuff.androidApp.presentation.list.schedule.all.AllScheduleViewModel
import co.softov.morestuff.androidApp.presentation.list.schedule.later.LaterScheduleViewModel
import co.softov.morestuff.androidApp.presentation.list.schedule.today.TodayScheduleViewModel
import co.softov.morestuff.androidApp.presentation.list.schedule.tomorrow.TomorrowScheduleViewModel
import co.softov.morestuff.androidApp.presentation.list.tasks.TaskListAdapter
import co.softov.morestuff.androidApp.presentation.list.tasks.active.ActiveTasksViewModel
import co.softov.morestuff.androidApp.presentation.list.tasks.complete.CompleteTasksViewModel
import co.softov.morestuff.androidapp.Constants.Companion.DATABASE_NAME
import co.softov.morestuff.db.StuffDb
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.KoinApiExtension
import org.koin.dsl.module

@OptIn(KoinApiExtension::class)
val appModule = module {

    // Debugging
    single<Debug>(createdAtStart = true) { Debugger(androidApplication()) }

    // Database
    single { createDatabase(androidApplication()) }

    // Repositories
    single<TaskRepository> {
        TaskRepositoryImpl(
            database = get(),
            mapTaskDb = makeTaskDbMapper()
        )
    }

    single<MessageRepository> {
        MessageRepositoryImpl(
            database = get(),
            mapMessageDb = makeMessageDbMapper()
        )
    }

    single<ScheduleRepository> {
        ScheduleRepositoryImpl(
            database = get(),
            mapScheduleDb = makeScheduleDbMapper(),
            mapScheduleWithTitleDb = makeScheduleWithTitleDbMapper()
        )
    }

    single<PreferenceRepository> {
        PreferenceRepositoryImpl(
            getSharedPreferences(
                androidContext()
            )
        )
    }

    // Service
    single<Scheduler> { SchedulerImpl(androidContext()) }
    single<Notifier> { NotifierImpl(androidContext()) }

    // Adapters
    factory { TaskListAdapter() }
    factory { ScheduleListAdapter() }

    // Platform specific use-cases
    factory<GetPagedMessages> {
        GetPagedMessagesImpl(
            database = get(),
            mapMessageDb = makeMessageDbMapper()
        )
    }

}

val presentationModule = module {
    // ViewModel
    viewModel { (conductor: ContentConductor) ->
        ContentViewModel(
            conductor = conductor,
            createNewTask = get(),
            getPagedMessages = get(),
            debug = get()
        )
    }
    viewModel {
        ActiveTasksViewModel(
            getActiveTasks = get(),
            rescheduleTask = get(),
            setTaskComplete = get()
        )
    }
    viewModel {
        CompleteTasksViewModel(
            getCompleteTasks = get(),
            rescheduleTask = get(),
            setTaskComplete = get()
        )
    }
    viewModel {
        TodayScheduleViewModel(
            getSchedulesWithTitle = get(),
            rescheduleTask = get(),
            setTaskComplete = get()
        )
    }
    viewModel {
        TomorrowScheduleViewModel(
            getSchedulesWithTitle = get(),
            rescheduleTask = get(),
            setTaskComplete = get()
        )
    }
    viewModel {
        LaterScheduleViewModel(
            getSchedulesWithTitle = get(),
            rescheduleTask = get(),
            setTaskComplete = get()
        )
    }
    viewModel {
        AllScheduleViewModel(
            getSchedulesWithTitle = get(),
            rescheduleTask = get(),
            setTaskComplete = get()
        )
    }
    viewModel { (conductor: DashboardConductor) ->
        DashboardViewModel(
            preferenceRepository = get(),
            createNewTask = get(),
            conductor = conductor
        )
    }
}

val domainModule = module {

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
        CreateScheduleImpl(scheduleRepository = get(), scheduler = get())
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
            preferenceRepository = get(),
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

internal fun getSharedPreferences(context: Context): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(context)
}

internal fun createDatabase(context: Context): StuffDb {
    val messageAdapter = Message.Adapter(
        typeAdapter = EnumColumnAdapter(),
        reply_typeAdapter = EnumColumnAdapter()
    )

    val scheduleAdapter = Schedule.Adapter(
        typeAdapter = EnumColumnAdapter()
    )

    return StuffDb(
        AndroidSqliteDriver(
            StuffDb.Schema,
            context,
            DATABASE_NAME
        ),
        messageAdapter = messageAdapter,
        scheduleAdapter = scheduleAdapter
    )
}
package co.softov.morestuff.android.domain.redux

import co.softov.morestuff.android.domain.redux.middleware.DevMiddleware
import co.softov.morestuff.android.domain.redux.middleware.ErrorMiddleware
import co.softov.morestuff.android.domain.redux.middleware.LoggerMiddleware
import co.softov.morestuff.android.domain.redux.middleware.MessageMiddleware
import co.softov.morestuff.android.domain.redux.middleware.NotificationMiddleware
import co.softov.morestuff.android.domain.redux.middleware.PriorityMiddleware
import co.softov.morestuff.android.domain.redux.middleware.ReminderMiddleware
import co.softov.morestuff.android.domain.redux.middleware.ScheduleMiddleware
import co.softov.morestuff.android.domain.redux.middleware.ScopeMiddleware
import co.softov.morestuff.android.domain.redux.middleware.SettingsMiddleware
import co.softov.morestuff.android.domain.redux.middleware.TaskMiddleware
import co.softov.morestuff.android.domain.redux.state.reduceSettingState
import co.softov.morestuff.android.domain.redux.store.SimpleStore

class AppStore(
    logger: LoggerMiddleware,
    devMiddleware: DevMiddleware,
    errorMiddleware: ErrorMiddleware,
    taskMiddleware: TaskMiddleware,
    messageMiddleware: MessageMiddleware,
    scheduleMiddleware: ScheduleMiddleware,
    responseMiddleware: ReminderMiddleware,
    notificationMiddleware: NotificationMiddleware,
    settingsMiddleware: SettingsMiddleware,
    priorityMiddleware: PriorityMiddleware,
    scopeMiddleware: ScopeMiddleware
) : SimpleStore<AppState>(
    AppState(),
    listOf(
        AppState::reduceSettingState,
    ),
    listOf(
        logger,
        devMiddleware,
        errorMiddleware,
        settingsMiddleware,
        taskMiddleware,
        scheduleMiddleware,
        messageMiddleware,
        responseMiddleware,
        notificationMiddleware,
        priorityMiddleware,
        scopeMiddleware
    )
)

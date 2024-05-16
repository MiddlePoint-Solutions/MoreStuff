package io.middlepoint.morestuff.android.domain.redux

import io.middlepoint.morestuff.android.domain.redux.middleware.DevMiddleware
import io.middlepoint.morestuff.android.domain.redux.middleware.ErrorMiddleware
import io.middlepoint.morestuff.android.domain.redux.middleware.LoggerMiddleware
import io.middlepoint.morestuff.android.domain.redux.middleware.MessageMiddleware
import io.middlepoint.morestuff.android.domain.redux.middleware.NotificationMiddleware
import io.middlepoint.morestuff.android.domain.redux.middleware.PriorityMiddleware
import io.middlepoint.morestuff.android.domain.redux.middleware.ReminderMiddleware
import io.middlepoint.morestuff.android.domain.redux.middleware.ScheduleMiddleware
import io.middlepoint.morestuff.android.domain.redux.middleware.ScopeMiddleware
import io.middlepoint.morestuff.android.domain.redux.middleware.SettingsMiddleware
import io.middlepoint.morestuff.android.domain.redux.middleware.TaskMiddleware
import io.middlepoint.morestuff.android.domain.redux.state.reduceSettingState
import io.middlepoint.morestuff.android.domain.redux.store.SimpleStore

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

package co.softov.morestuff.android.domain.redux

import co.softov.morestuff.android.domain.redux.middleware.*
import co.softov.morestuff.android.domain.redux.state.PriorityMiddleware
import co.softov.morestuff.android.domain.redux.state.UserMiddleware
import co.softov.morestuff.android.domain.redux.state.reducePriorityState
import co.softov.morestuff.android.domain.redux.state.reduceUserState

class AppStore(
    logger: LoggerMiddleware,
    navigator: NavigationMiddleware,
    taskMiddleware: TaskMiddleware,
    messageMiddleware: MessageMiddleware,
    scheduleMiddleware: ScheduleMiddleware,
    responseMiddleware: ResponseMiddleware,
    notificationMiddleware: NotificationMiddleware,
    userMiddleware: UserMiddleware,
    priorityMiddleware: PriorityMiddleware,
) : SimpleStore<AppState>(
    AppState(),
    listOf(
        AppState::reduceUserState,
        AppState::reducePriorityState,
    ),
    listOf(
        logger,
        userMiddleware,
        taskMiddleware,
        scheduleMiddleware,
        messageMiddleware,
        responseMiddleware,
        notificationMiddleware,
        priorityMiddleware,
        navigator
    )
)

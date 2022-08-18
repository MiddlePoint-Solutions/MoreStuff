package co.softov.morestuff.android.domain.redux

import co.softov.morestuff.android.domain.redux.middleware.*
import co.softov.morestuff.android.domain.redux.state.DashboardMiddleware
import co.softov.morestuff.android.domain.redux.state.UserMiddleware
import co.softov.morestuff.android.domain.redux.state.reduceDashboardState
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
    dashboardMiddleware: DashboardMiddleware,
) : SimpleStore<AppState>(
    AppState(),
    listOf(
        AppState::reduceUserState,
        AppState::reduceDashboardState,
    ),
    listOf(
        logger,
        userMiddleware,
        taskMiddleware,
        scheduleMiddleware,
        messageMiddleware,
        responseMiddleware,
        notificationMiddleware,
        dashboardMiddleware,
        navigator
    )
)

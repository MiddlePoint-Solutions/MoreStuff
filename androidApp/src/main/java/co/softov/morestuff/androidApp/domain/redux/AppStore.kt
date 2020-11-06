package co.softov.morestuff.androidApp.domain.redux

import co.softov.morestuff.androidApp.domain.redux.middleware.*
import co.softov.morestuff.androidApp.domain.redux.state.reduceSignInState
import com.iiitech.operations.domain.redux.SimpleStore

class AppStore(
    logger: LoggerMiddleware,
    navigator: NavigationMiddleware,
    taskMiddleware: TaskMiddleware,
    messageMiddleware: MessageMiddleware,
    scheduleMiddleware: ScheduleMiddleware,
    responseMiddleware: ResponseMiddleware
) : SimpleStore<AppState>(
    AppState(),
    listOf(AppState::reduceSignInState),
    listOf(
        logger,
        navigator,
        taskMiddleware,
        scheduleMiddleware,
        messageMiddleware,
        responseMiddleware
    )
)

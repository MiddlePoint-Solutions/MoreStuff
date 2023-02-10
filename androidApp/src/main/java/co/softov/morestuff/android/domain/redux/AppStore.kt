package co.softov.morestuff.android.domain.redux

import co.softov.morestuff.android.domain.redux.middleware.*
import co.softov.morestuff.android.domain.redux.middleware.PriorityMiddleware
import co.softov.morestuff.android.domain.redux.middleware.SettingsMiddleware
import co.softov.morestuff.android.domain.redux.state.reducePriorityState
import co.softov.morestuff.android.domain.redux.state.reduceReviewState
import co.softov.morestuff.android.domain.redux.state.reduceSettingState

class AppStore(
    logger: LoggerMiddleware,
    errorMiddleware: ErrorMiddleware,
    navigator: NavigationMiddleware,
    taskMiddleware: TaskMiddleware,
    messageMiddleware: MessageMiddleware,
    scheduleMiddleware: ScheduleMiddleware,
    responseMiddleware: ReminderMiddleware,
    notificationMiddleware: NotificationMiddleware,
    settingsMiddleware: SettingsMiddleware,
    priorityMiddleware: PriorityMiddleware,
    reviewMiddleware: ReviewMiddleware,


    ) : SimpleStore<AppState>(
    AppState(),
    listOf(
        AppState::reduceSettingState,
        AppState::reducePriorityState,
        AppState::reduceReviewState,
    ),
    listOf(
        logger,
        errorMiddleware,
        settingsMiddleware,
        taskMiddleware,
        scheduleMiddleware,
        messageMiddleware,
        responseMiddleware,
        notificationMiddleware,
        priorityMiddleware,
        reviewMiddleware,
        navigator
    )
)

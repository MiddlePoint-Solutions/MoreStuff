package co.softov.morestuff.android.domain.redux

import co.softov.morestuff.android.domain.redux.middleware.*
import co.softov.morestuff.android.domain.redux.middleware.SettingsMiddleware
import co.softov.morestuff.android.domain.redux.state.reduceReviewState
import co.softov.morestuff.android.domain.redux.state.reduceSettingState

class AppStore(
    logger: LoggerMiddleware,
    devMiddleware: DevMiddleware,
    errorMiddleware: ErrorMiddleware,
    navigator: NavigationMiddleware,
    taskMiddleware: TaskMiddleware,
    messageMiddleware: MessageMiddleware,
    scheduleMiddleware: ScheduleMiddleware,
    responseMiddleware: ReminderMiddleware,
    notificationMiddleware: NotificationMiddleware,
    settingsMiddleware: SettingsMiddleware,
    reviewMiddleware: ReviewMiddleware,
) : SimpleStore<AppState>(
    AppState(),
    listOf(
        AppState::reduceSettingState,
        AppState::reduceReviewState,
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
        reviewMiddleware,
        navigator
    )
)

package io.middlepoint.morestuff.shared.data.middleware

import io.middlepoint.morestuff.shared.domain.redux.MiddlewareProvider
import io.middlepoint.morestuff.shared.domain.redux.state.AppState

class MiddlewareProviderImpl(
  logger: LoggerMiddleware,
  activityMiddleware: ActivityMiddleware,
  devMiddleware: DevMiddleware,
  errorMiddleware: ErrorMiddleware,
  authMiddleware: AuthMiddleware,
  taskMiddleware: TaskMiddleware,
  messageMiddleware: MessageMiddleware,
  scheduleMiddleware: ScheduleMiddleware,
  notificationMiddleware: NotificationMiddleware,
  settingsMiddleware: SettingsMiddleware,
  priorityMiddleware: PriorityMiddleware,
  scopeMiddleware: ScopeMiddleware,
  syncMiddleware: SyncMiddleware
) : MiddlewareProvider<AppState> {

  override val middlewareOrder = listOf(
    activityMiddleware,
    logger,
    devMiddleware,
    errorMiddleware,
    authMiddleware,
    syncMiddleware,
    settingsMiddleware,
    taskMiddleware,
    scheduleMiddleware,
    messageMiddleware,
    notificationMiddleware,
    priorityMiddleware,
    scopeMiddleware
  )

}
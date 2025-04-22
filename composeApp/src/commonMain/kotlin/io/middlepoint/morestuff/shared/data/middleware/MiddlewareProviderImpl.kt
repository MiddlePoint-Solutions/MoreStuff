package io.middlepoint.morestuff.shared.data.middleware

import io.middlepoint.morestuff.shared.domain.redux.MiddlewareProvider
import io.middlepoint.morestuff.shared.domain.redux.state.AppState

class MiddlewareProviderImpl(
  logger: LoggerMiddleware,
  devMiddleware: DevMiddleware,
  errorMiddleware: ErrorMiddleware,
  authMiddleware: AuthMiddleware,
  taskMiddleware: TaskMiddleware,
  messageMiddleware: MessageMiddleware,
  scheduleMiddleware: ScheduleMiddleware,
  notificationMiddleware: NotificationMiddleware,
  settingsMiddleware: SettingsMiddleware,
  priorityMiddleware: PriorityMiddleware,
  scopeMiddleware: ScopeMiddleware
) : MiddlewareProvider<AppState> {

  override val middlewareOrder = listOf(
    logger,
    devMiddleware,
    errorMiddleware,
    authMiddleware,
    settingsMiddleware,
    taskMiddleware,
    scheduleMiddleware,
    messageMiddleware,
    notificationMiddleware,
    priorityMiddleware,
    scopeMiddleware
  )

}
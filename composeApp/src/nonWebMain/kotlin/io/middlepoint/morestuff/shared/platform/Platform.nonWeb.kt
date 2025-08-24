package io.middlepoint.morestuff.shared.platform

import MoreStuff.composeApp.BuildConfig
import io.sentry.kotlin.multiplatform.Sentry

actual fun initializeSentry() {
  Sentry.init { options ->
    options.dsn = BuildConfig.SENTRY_DSN
    options.debug = false
  }
}

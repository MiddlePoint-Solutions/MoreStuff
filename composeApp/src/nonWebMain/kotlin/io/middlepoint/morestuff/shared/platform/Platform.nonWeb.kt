package io.middlepoint.morestuff.shared.platform

import io.sentry.kotlin.multiplatform.Sentry

actual fun initializeSentry() {
  Sentry.init { options ->
    options.dsn = "https://bc0a969f01468e75bd5473e27a205277@o4509511098040320.ingest.de.sentry.io/4509511355531344"
    options.debug = false
  }
}

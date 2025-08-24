package io.middlepoint.morestuff.shared

import io.middlepoint.morestuff.shared.di.initKoin
import io.middlepoint.morestuff.shared.domain.service.NavigationHelper
import io.middlepoint.morestuff.shared.platform.initializeSentry
import org.koin.dsl.module

fun startSdk(
    navigationHelper: NavigationHelper
) {
    initializeSentry()
    initKoin {
        modules(
            module {
                single { navigationHelper }
            }
        )
    }
}
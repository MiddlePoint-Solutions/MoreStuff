package io.middlepoint.morestuff.shared

import io.middlepoint.morestuff.shared.di.initKoin
import io.middlepoint.morestuff.shared.domain.service.NavigationHelper
import org.koin.dsl.module

fun startSdk(
    navigationHelper: NavigationHelper
) {
    initKoin {
        modules(
            module {
                single { navigationHelper }
            }
        )
    }
}
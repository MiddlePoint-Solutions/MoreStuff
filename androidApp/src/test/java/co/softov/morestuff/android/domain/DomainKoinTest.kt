package co.softov.morestuff.android.domain

import co.softov.morestuff.android.di.domainModules
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.koin.compose.getKoin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

interface DomainKoinTest : KoinTest {
    companion object {

        @BeforeAll
        @JvmStatic
        fun setup() {
            startKoin {
                modules(domainModules)
            }
        }

        @AfterAll
        fun cleanup() {
            stopKoin()
        }

    }
}
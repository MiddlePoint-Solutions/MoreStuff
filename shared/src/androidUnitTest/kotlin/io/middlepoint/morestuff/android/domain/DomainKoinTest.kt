package io.middlepoint.morestuff.android.domain

import io.middlepoint.morestuff.shared.di.domainModules
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
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

    @JvmStatic
    @AfterAll
    fun cleanup(): Unit {
      stopKoin()
    }

  }
}
package io.middlepoint.morestuff.shared.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
  appDeclaration()
  modules(domainModules)
  modules(platformModule, dataModule, presentationModule)
}

expect val platformModule: Module
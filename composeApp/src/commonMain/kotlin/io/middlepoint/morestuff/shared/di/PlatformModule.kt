package io.middlepoint.morestuff.shared.di

import io.middlepoint.morestuff.shared.sharedModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(supabaseModule)
    modules(domainModules)
    modules(platformModule, sharedModule, dataModule, presentationModule)
}

expect val platformModule: Module
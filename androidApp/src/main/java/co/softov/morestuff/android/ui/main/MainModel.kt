package co.softov.morestuff.android.ui.main

import co.softov.morestuff.android.domain.enums.AppTheme

data class MainModel(
    val theme: AppTheme = AppTheme.MODE_AUTO,
    val showOnBoarding: Boolean = false,
)

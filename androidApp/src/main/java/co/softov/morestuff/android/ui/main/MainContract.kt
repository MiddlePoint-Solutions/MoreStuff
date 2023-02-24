package co.softov.morestuff.android.ui.main

import co.softov.morestuff.android.app.presentation.navigation.BaseConductor

// TODO: stop using conductor pattern

interface MainConductor : BaseConductor {
    fun showTaskList()
}


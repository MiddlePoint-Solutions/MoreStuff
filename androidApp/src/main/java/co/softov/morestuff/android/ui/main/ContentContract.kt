package co.softov.morestuff.android.ui.main

import co.softov.morestuff.android.app.presentation.navigation.BaseConductor

interface ContentConductor : BaseConductor {
    fun showTaskList()
    fun showTodayTimePicker()
    fun showTomorrowTimePicker()
    fun showDateTimePicker()
}


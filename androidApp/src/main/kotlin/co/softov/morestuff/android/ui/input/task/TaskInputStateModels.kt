package co.softov.morestuff.android.ui.input.task

sealed class TaskInputEvent {
    data object SetNowPriority : TaskInputEvent()
    data object SetLaterPriority : TaskInputEvent()
    data object SetPlanPriority : TaskInputEvent()
    data class UpdatePlanDate(val utcTimeMillis: Long) : TaskInputEvent()
    data class UpdatePlanTime(val hour: Int, val minute: Int) : TaskInputEvent()

}
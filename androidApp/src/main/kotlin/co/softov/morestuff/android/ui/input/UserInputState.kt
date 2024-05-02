package co.softov.morestuff.android.ui.input

import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.model.defaultScope
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.util.TimeFormatter
import co.softov.morestuff.android.ui.model.MessageUiModel
import co.softov.morestuff.android.ui.model.PriorityInputUiModel
import co.softov.morestuff.android.ui.model.PriorityUiModel




sealed class UserInputEvent {
    data class SetPriority(val priority: PriorityUiModel) : UserInputEvent()
    data class UpdatePlanDate(val utcTimeMillis: Long) : UserInputEvent()
    data class UpdatePlanTime(val hour: Int, val minute: Int) : UserInputEvent()
    data class SetCurrentScope(val scopeId: Long) : UserInputEvent()
    data class CreateNewTask(val title: String) : UserInputEvent()
    data class SetLaterPriority (val priority: PriorityUiModel) : UserInputEvent()
    data class SetNowPriority (val priority: PriorityUiModel) : UserInputEvent()
    data object SetPlanPriority  : UserInputEvent()
}

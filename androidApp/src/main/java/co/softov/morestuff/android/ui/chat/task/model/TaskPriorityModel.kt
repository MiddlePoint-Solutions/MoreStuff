package co.softov.morestuff.android.ui.chat.task.model

import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.presentation.model.PriorityOptionsModel

data class TaskPriorityModel(
    val priorityModel: PriorityOptionsModel = PriorityOptionsModel(
        current = Priority.today,
        options = listOf()
    ),
    val showConfirmation: Boolean = false
)
package co.softov.morestuff.android.ui.chat.task.model

import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PriorityOptionsModel

data class TaskPriorityModel(
    val priorityModel: PriorityOptionsModel = PriorityOptionsModel(
        priority = Priority.Now(),
        options = listOf()
    ),
    val showConfirmation: Boolean = false
)
package io.middlepoint.morestuff.shared.data.sync

import io.middlepoint.morestuff.db.Messages
import io.middlepoint.morestuff.db.Messages_extra
import io.middlepoint.morestuff.db.Schedules
import io.middlepoint.morestuff.db.Scopes
import io.middlepoint.morestuff.db.Tasks
import io.middlepoint.morestuff.db.Tasks_scopes

data class LocalData(
    val tasks: List<Tasks>,
    val scopes: List<Scopes>,
    val messages: List<Messages>,
    val tasksScopes: List<Tasks_scopes>,
    val messageExtras: List<Messages_extra>,
    val schedules: List<Schedules>
) {

    fun maxUpdatedAt() =
        (tasks.map { it.updated_at }
                + scopes.map { it.updated_at }
                + messages.map { it.updated_at }
                + tasksScopes.map { it.updated_at }
                + messageExtras.map { it.updated_at }
                + schedules.map { it.updated_at })
            .maxOrNull()

    fun containsChanges() =
        tasks.isNotEmpty() ||
                scopes.isNotEmpty() ||
                messages.isNotEmpty() ||
                tasksScopes.isNotEmpty() ||
                messageExtras.isNotEmpty() ||
                schedules.isNotEmpty()

}
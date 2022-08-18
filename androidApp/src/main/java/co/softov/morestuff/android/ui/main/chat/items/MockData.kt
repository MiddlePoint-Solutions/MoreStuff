package co.softov.morestuff.android.ui.main.chat.items

import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.ui.main.chat.ChatActions

object MockData {

    val chatActions = ChatActions(confirmationAction = { _, _ -> }, scheduleAction = { _, _ -> })

    object Message {

        val userNewTask =
            Message(
                0,
                0,
                0,
                ContentType.USER_NEW_TASK,
                "The big bang",
                null,
                content = "Hello there!",
                null,
                null,
                null
            )

    }

}
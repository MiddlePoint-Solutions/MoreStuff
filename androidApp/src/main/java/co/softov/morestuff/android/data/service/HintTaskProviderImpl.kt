package co.softov.morestuff.android.data.service

import android.content.Context
import android.content.res.Resources
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.service.HintTaskProvider

class HintTaskProviderImpl(context: Context) : HintTaskProvider {

    private val resources: Resources = context.resources
    override fun getTaskTitles(): List<String> {

        return listOf(
            resources.getString(R.string.hint_task_plan),
            resources.getString(R.string.hint_task_later),
            resources.getString(R.string.hint_task_now),
        )
    }
    override fun getTaskMessages(taskName: String, priority: Priority): List<MessageHint> {
        return when (taskName) {
            resources.getString(R.string.hint_task_now) -> listOf(

                MessageHint(resources.getString(R.string.message_4_now_task)),
                MessageHint(resources.getString(R.string.message_3_now_task)),
                MessageHint(resources.getString(R.string.message_2_now_task)),
                MessageHint(resources.getString(R.string.message_1_now_task)),
                )
            resources.getString(R.string.hint_task_later) -> listOf(
                MessageHint(resources.getString(R.string.message_2_later_task)),
                MessageHint(resources.getString(R.string.message_1_later_task))
            )

            resources.getString(R.string.hint_task_plan) -> listOf(
                (MessageHint(resources.getString(R.string.message_1_plan_task))),
            )


            else -> emptyList()
        }
    }
}
data class MessageHint(val content: String)
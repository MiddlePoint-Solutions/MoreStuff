package co.softov.morestuff.android.data.service

import android.content.Context
import android.content.res.Resources
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.service.HintTask


data class HintTaskName(val taskTitle: String, val taskMessages: List<HintMessage>)
data class HintMessage(val content: String)

class HintTaskImpl(context: Context) : HintTask {
    private val resources: Resources = context.resources

    override fun getHintTasks(): List<HintTaskName> {
        return listOf(
            HintTaskName(
                taskTitle = resources.getString(R.string.hint_task_plan),
                taskMessages = listOf(
                    HintMessage(resources.getString(R.string.message_1_plan_task))
                )
            ),
            HintTaskName(
                taskTitle = resources.getString(R.string.hint_task_later),
                taskMessages = listOf(
                    HintMessage(resources.getString(R.string.message_2_later_task)),
                    HintMessage(resources.getString(R.string.message_1_later_task))
                )
            ),
            HintTaskName(
                taskTitle = resources.getString(R.string.hint_task_now),
                taskMessages = listOf(
                    HintMessage(resources.getString(R.string.message_4_now_task)),
                    HintMessage(resources.getString(R.string.message_3_now_task)),
                    HintMessage(resources.getString(R.string.message_2_now_task)),
                    HintMessage(resources.getString(R.string.message_1_now_task))
                )
            )
        )
    }
}


package co.softov.morestuff.android.data.service

import android.content.Context
import android.content.res.Resources
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.HintMessage
import co.softov.morestuff.android.domain.model.HintTask
import co.softov.morestuff.android.domain.service.HintTaskProvider


class HintTaskProviderImpl(context: Context) : HintTaskProvider {
    private val resources: Resources = context.resources

    override fun getHintTasks(): List<HintTask> {
        return listOf(
            HintTask(
                taskTitle = resources.getString(R.string.hint_task_1),
                taskMessages = listOf(
                    HintMessage(resources.getString(R.string.hint_task_1_message_3)),
                    HintMessage(resources.getString(R.string.hint_task_1_message_2)),
                    HintMessage(resources.getString(R.string.hint_task_1_message_1))
                )
            )
        )
    }
}


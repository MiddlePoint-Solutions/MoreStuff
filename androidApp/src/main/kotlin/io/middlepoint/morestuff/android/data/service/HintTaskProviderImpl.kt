package io.middlepoint.morestuff.android.data.service

import android.content.Context
import android.content.res.Resources
import io.middlepoint.morestuff.android.R
import io.middlepoint.morestuff.shared.domain.model.HintMessage
import io.middlepoint.morestuff.shared.domain.model.HintTask
import io.middlepoint.morestuff.shared.domain.service.HintTaskProvider


class HintTaskProviderImpl(context: Context) : HintTaskProvider {
    private val resources: Resources = context.resources

    override fun getHintTasks(): List<HintTask> {
        return listOf(
            HintTask(
                taskTitle = resources.getString(R.string.hint_task_1),
                taskMessages = listOf(
                    HintMessage(resources.getString(R.string.hint_task_1_message_4)),
                    HintMessage(resources.getString(R.string.hint_task_1_message_3)),
                    HintMessage(resources.getString(R.string.hint_task_1_message_2)),
                    HintMessage(resources.getString(R.string.hint_task_1_message_1)),
                )
            )
        )
    }
}


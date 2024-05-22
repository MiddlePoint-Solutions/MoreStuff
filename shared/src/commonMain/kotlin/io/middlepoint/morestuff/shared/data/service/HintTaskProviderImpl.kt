@file:OptIn(ExperimentalResourceApi::class)

package io.middlepoint.morestuff.shared.data.service

import io.middlepoint.morestuff.shared.domain.model.HintTask
import io.middlepoint.morestuff.shared.domain.service.HintTaskProvider
import morestuff.shared.generated.resources.Res
import morestuff.shared.generated.resources.hint_task_1
import morestuff.shared.generated.resources.hint_task_1_message_1
import morestuff.shared.generated.resources.hint_task_1_message_2
import morestuff.shared.generated.resources.hint_task_1_message_3
import morestuff.shared.generated.resources.hint_task_1_message_4
import org.jetbrains.compose.resources.ExperimentalResourceApi


class HintTaskProviderImpl() : HintTaskProvider {

  override fun getHintTasks(): List<HintTask> {
    return listOf(
      HintTask(
        taskTitle = Res.string.hint_task_1,
        taskMessages = listOf(
          Res.string.hint_task_1_message_4,
          Res.string.hint_task_1_message_3,
          Res.string.hint_task_1_message_2,
          Res.string.hint_task_1_message_1,
        )
      )
    )
  }
}


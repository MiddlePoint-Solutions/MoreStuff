package io.middlepoint.morestuff.shared.domain.model

import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.model.core.TaskDomain

data class ReviewTasks(
  val tasks: List<TaskDomain>,
  val taskMessages: Map<Long, List<Message>>
)

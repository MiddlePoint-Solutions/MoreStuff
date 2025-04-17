package io.middlepoint.morestuff.shared.domain.model

import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.model.core.Task

data class ReviewTasks(
  val tasks: List<Task>,
  val taskMessages: Map<Uuid, List<Message>>
)

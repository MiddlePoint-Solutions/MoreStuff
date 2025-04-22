package io.middlepoint.morestuff.shared.domain.usecase.message

import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface GetTaskChatMessagesUseCase {
  operator fun invoke(taskId: Uuid): GetTaskChatMessagesUseCase
  fun asList(): List<Message>
  fun asFlow(): Flow<List<Message>>
}

class GetTaskChatMessagesUseCaseImpl(
  private val messageRepository: MessageRepository,
) : GetTaskChatMessagesUseCase {

  private var taskId: Uuid? = null

  override fun invoke(taskId: Uuid): GetTaskChatMessagesUseCase {
    this.taskId = taskId
    return this
  }

  override fun asList(): List<Message> =
    taskId?.let(messageRepository::getTaskChatMessages) ?: listOf()

  override fun asFlow(): Flow<List<Message>> =
    taskId?.let(messageRepository::getTaskChatMessagesFlow) ?: flowOf()
}

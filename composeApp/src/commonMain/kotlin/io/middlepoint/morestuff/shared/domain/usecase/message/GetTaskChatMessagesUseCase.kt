package io.middlepoint.morestuff.shared.domain.usecase.message

import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow

interface GetTaskChatMessagesUseCase {
  operator fun invoke(taskId: Long): GetTaskChatMessagesUseCase
  fun asList(): List<Message>
  fun asFlow(): Flow<List<Message>>
}

class GetTaskChatMessagesUseCaseImpl(
  private val messageRepository: MessageRepository,
) : GetTaskChatMessagesUseCase {

  private var taskId: Long = 0

  override fun invoke(taskId: Long): GetTaskChatMessagesUseCase {
    this.taskId = taskId
    return this
  }

  override fun asList(): List<Message> = messageRepository.getTaskChatMessages(taskId)

  override fun asFlow(): Flow<List<Message>> = messageRepository.getTaskChatMessagesFlow(taskId)
}

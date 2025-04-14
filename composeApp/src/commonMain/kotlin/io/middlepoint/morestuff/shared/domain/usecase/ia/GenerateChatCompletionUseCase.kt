package io.middlepoint.morestuff.shared.domain.usecase.ia

import io.middlepoint.morestuff.shared.domain.repository.LlmRepository
import io.middlepoint.morestuff.shared.domain.usecase.message.GetTaskChatMessagesUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskUseCase

interface GenerateChatCompletionUseCase {
    suspend operator fun invoke(prompt: String, taskId: Long): String
}

class GenerateChatCompletionUseCaseImpl(
    private val llmRepository: LlmRepository,
    private val getTaskChatMessagesUseCase: GetTaskChatMessagesUseCase,
    private val getTaskUseCase: GetTaskUseCase
) : GenerateChatCompletionUseCase {
    override suspend fun invoke(prompt: String, taskId: Long): String {
        val messages = getTaskChatMessagesUseCase(taskId).asList()
        val task = getTaskUseCase(taskId)
        val fullPrompt = "$task $messages\n\nNew message: $prompt"
        return llmRepository.generateChatCompletion(fullPrompt)
    }

}
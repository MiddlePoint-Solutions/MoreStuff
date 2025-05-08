package io.middlepoint.morestuff.shared.domain.usecase.ia

import io.middlepoint.morestuff.shared.domain.repository.LlmRepository
import kotlinx.coroutines.flow.Flow

interface StreamChatCompletionUseCase {
    operator fun invoke(prompt: String): Flow<String>
}

class StreamChatCompletionUseCaseImpl(
    private val llmRepository: LlmRepository
) : StreamChatCompletionUseCase {
    override fun invoke(prompt: String): Flow<String> {
        return llmRepository.streamChatCompletion(prompt)
    }
}
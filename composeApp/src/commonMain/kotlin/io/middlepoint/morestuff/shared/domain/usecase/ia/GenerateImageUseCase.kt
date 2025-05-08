package io.middlepoint.morestuff.shared.domain.usecase.ia

import io.middlepoint.morestuff.shared.domain.repository.LlmRepository

interface GenerateImageUseCase {
    suspend operator fun invoke(prompt: String): String
}

class GenerateImageUseCaseImpl(
    private val llmRepository: LlmRepository
) : GenerateImageUseCase {
    override suspend fun invoke(prompt: String): String {
        return llmRepository.generateImage(prompt)
    }
}
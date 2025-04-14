package io.middlepoint.morestuff.shared.domain.usecase.settings

import io.middlepoint.morestuff.shared.domain.repository.LlmRepository

interface GetApiKeyUseCase {
    operator fun invoke(): String
}

class GetApiKeyUseCaseImpl(
    private val llmRepository: LlmRepository
) : GetApiKeyUseCase {
    override fun invoke(): String =
        llmRepository.getApiKey() ?: throw IllegalStateException("API key not found")
}
package io.middlepoint.morestuff.shared.domain.usecase.settings

import io.middlepoint.morestuff.shared.domain.repository.OpenAIRepository

interface GetApiKeyUseCase {
    operator fun invoke(): String
}

class GetApiKeyUseCaseImpl(
    private val openAIRepository: OpenAIRepository
) : GetApiKeyUseCase {
    override fun invoke(): String =
        openAIRepository.getApiKey() ?: throw IllegalStateException("API key not found")
}
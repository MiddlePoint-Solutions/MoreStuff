package io.middlepoint.morestuff.shared.domain.usecase.settings

import io.middlepoint.morestuff.shared.domain.repository.OpenAIRepository

interface SaveApiKeyUseCase {
    operator fun invoke(apiKey: String)
}

class SaveApiKeyUseCaseImpl(
    private val openAIRepository: OpenAIRepository
) : SaveApiKeyUseCase {
    override fun invoke(apiKey: String) {
        openAIRepository.saveApiKey(apiKey)
    }
}
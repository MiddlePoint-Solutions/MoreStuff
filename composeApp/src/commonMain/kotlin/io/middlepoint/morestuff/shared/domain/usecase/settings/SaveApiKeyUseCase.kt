package io.middlepoint.morestuff.shared.domain.usecase.settings

import io.middlepoint.morestuff.shared.domain.repository.LlmRepository

interface SaveApiKeyUseCase {
    operator fun invoke(apiKey: String)
}

class SaveApiKeyUseCaseImpl(
    private val llmRepository: LlmRepository
) : SaveApiKeyUseCase {
    override fun invoke(apiKey: String) {
        llmRepository.saveApiKey(apiKey)
    }
}
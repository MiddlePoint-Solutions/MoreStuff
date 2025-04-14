package io.middlepoint.morestuff.shared.domain.repository

import kotlinx.coroutines.flow.Flow

interface LlmRepository {
    suspend fun generateChatCompletion(prompt: String): String
    suspend fun generateImage(prompt: String): String
    fun streamChatCompletion(prompt: String): Flow<String>
    fun saveApiKey(apiKey: String)
    fun getApiKey(): String?
}
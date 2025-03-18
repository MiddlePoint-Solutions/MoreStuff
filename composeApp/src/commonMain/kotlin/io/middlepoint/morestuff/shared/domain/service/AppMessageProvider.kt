package io.middlepoint.morestuff.shared.domain.service

interface AppMessageProvider {
    suspend fun getNewTaskAddedMessage(): String
    suspend fun getWhatCanIDoForYouMessage(): String
}

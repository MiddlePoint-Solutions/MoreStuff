package io.middlepoint.morestuff.shared.domain.service

interface AppMessageProvider {
    fun getNewTaskAddedMessage(): String
    fun getWhatCanIDoForYouMessage(): String
}

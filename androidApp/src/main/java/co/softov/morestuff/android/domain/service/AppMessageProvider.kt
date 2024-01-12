package co.softov.morestuff.android.domain.service

interface AppMessagesProvider {
    fun getNewTaskAddedMessage(): String
    fun getWhatCanIDoForYouMessage(): String
}

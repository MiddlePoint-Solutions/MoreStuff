package co.softov.morestuff.android.domain.service

interface AppMessageProvider {
    fun getNewTaskAddedMessage(): String
    fun getWhatCanIDoForYouMessage(): String
}

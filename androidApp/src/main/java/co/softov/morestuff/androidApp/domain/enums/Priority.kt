package co.softov.morestuff.androidApp.domain.enums

sealed class Priority(val time: Long = 0) {
    data class Later(val at: Long = 0) : Priority(at)
    data class Today(val at: Long = 0) : Priority(at)
    data class Tomorrow(val at: Long = 0) : Priority(at)
}
package io.middlepoint.morestuff.android.domain.model

sealed class ChatContext(open val scopeId: Long) {

    data class Main(override val scopeId: Long) : ChatContext(scopeId)
    data class Share(val shareable: Shareable) : ChatContext(defaultScope.id)

}

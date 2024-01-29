package co.softov.morestuff.android.domain.model

import co.softov.morestuff.android.domain.nav.Shareable

sealed class ChatContext(open val scopeId: Long) {

    data class Main(override val scopeId: Long) : ChatContext(scopeId)
    data class Share(val shareable: Shareable) : ChatContext(defaultScope.id)

}

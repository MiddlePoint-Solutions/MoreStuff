package co.softov.morestuff.android.ui.chat.items

import kotlinx.serialization.Serializable

@Serializable
data class OpenGraphResult(
    var title: String? = null,
    var description: String? = null,
    var url: String? = null,
    var image: String? = null,
    var siteName: String? = null,
    var type: String? = null
)


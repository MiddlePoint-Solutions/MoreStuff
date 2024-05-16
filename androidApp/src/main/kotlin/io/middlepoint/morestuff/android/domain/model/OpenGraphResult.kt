package io.middlepoint.morestuff.android.domain.model

import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class OpenGraphResult(
    val title: String? = null,
    val description: String? = null,
    val url: String? = null,
    val image: String? = null,
    val siteName: String? = null,
    val type: String? = null
): Parcelable


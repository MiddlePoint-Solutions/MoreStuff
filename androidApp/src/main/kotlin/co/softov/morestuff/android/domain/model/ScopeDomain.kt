package co.softov.morestuff.android.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import javax.annotation.concurrent.Immutable

@Serializable
@Immutable
data class ScopeDomain(
    val id: Long,
    val uid: String,
    val name: String,
    val order: Int,
)

val defaultScope = ScopeDomain(
    id = 1,
    uid = "",
    name = "Stuff",
    order = 0,
)

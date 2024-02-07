package co.softov.morestuff.android.domain.model

import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.parcelize.Parcelize
import javax.annotation.concurrent.Immutable

@Parcelize
@Immutable
data class ScopeDomain(
    val id: Long,
    val uid: String,
    val name: String,
    val order: Int,
): Parcelable

val defaultScope = ScopeDomain(
    id = 1,
    uid = "",
    name = "Stuff",
    order = 0,
)

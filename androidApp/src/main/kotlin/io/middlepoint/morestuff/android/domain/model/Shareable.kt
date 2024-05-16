package io.middlepoint.morestuff.android.domain.model
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
sealed class Shareable : Parcelable {
    @Parcelize
    data class Text(val message: String) : Shareable()

    @Parcelize
    data class Image(val uris: String, val message: String) : Shareable()

    @Parcelize
    data class Pdf(val uris: String,val message: String) : Shareable()
}

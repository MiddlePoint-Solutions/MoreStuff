package co.softov.morestuff.android.domain.nav
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
sealed class Shareable : Parcelable {
    @Parcelize
    data class Text(val content: String) : Shareable()

    @Parcelize
    data class Image(val uris: String, val message: String) : Shareable()
}

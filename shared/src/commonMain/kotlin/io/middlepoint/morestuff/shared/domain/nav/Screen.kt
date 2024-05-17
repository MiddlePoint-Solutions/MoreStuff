package io.middlepoint.morestuff.shared.domain.nav

import android.net.Uri
import android.os.Parcelable
import io.middlepoint.morestuff.shared.domain.model.Shareable
import kotlinx.parcelize.Parcelize


sealed class Screen : Parcelable {

    @Parcelize
    data object OnBoarding : Screen()

    @Parcelize
    data object Home : Screen()

    @Parcelize
    data class Review(val scopeId: Long) : Screen()

    @Parcelize
    data object Settings : Screen()

    @Parcelize
    data class TaskChat(val taskId: Long) : Screen()

    @Parcelize
    data class Share(val shareable: Shareable, val content: String) : Screen()

    @Parcelize
    data class ImagePreview(val imageUri: Uri, val taskId: Long) : Screen()

    @Parcelize
    data object Scopes : Screen()

    @Parcelize
    data class CreateScope(val onSave: (String) -> Unit) : Screen()
}

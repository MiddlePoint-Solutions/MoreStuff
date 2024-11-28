package io.middlepoint.morestuff.shared.domain.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.domain.model.Shareable
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow


val logger = Logger.withTag("NavigationHelper")

class NavigationHelper : ViewModel() {

    val shareable = MutableSharedFlow<Shareable>()

    fun shareText(message: String) {
        viewModelScope.launch {
            logger.d { "Emitting Shareable.Text with message: $message" }
            shareable.emit(Shareable.Text(message))
        }
    }

    fun shareImage(uri: String) {
        viewModelScope.launch {
            logger.d { "Emitting Shareable.Image with uri: $uri," }
            shareable.emit(Shareable.Image(uri, ""))
        }
    }

    fun sharePdf(uri: String) {
        viewModelScope.launch {
            logger.d { "Emitting Shareable.Pdf with uri: $uri" }
            shareable.emit(Shareable.Pdf(uri, ""))
        }
    }
}





/*class NavigationHelper : ViewModel()  {

    val shareable = MutableSharedFlow<String>()

    fun share(path: String) {
        viewModelScope.launch {
            logger.d { "Emitting path to shareable: $path" }
            shareable.emit(path) }
    }

}*/

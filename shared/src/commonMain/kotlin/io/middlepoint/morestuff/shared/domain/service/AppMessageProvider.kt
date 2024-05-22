package io.middlepoint.morestuff.shared.domain.service

import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource

@OptIn(ExperimentalResourceApi::class)
interface AppMessageProvider {
    fun getNewTaskAddedMessage(): StringResource
    fun getWhatCanIDoForYouMessage(): StringResource
}

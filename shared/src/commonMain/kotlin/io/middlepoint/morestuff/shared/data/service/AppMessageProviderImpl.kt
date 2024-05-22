package io.middlepoint.morestuff.shared.data.service

import io.middlepoint.morestuff.shared.domain.service.AppMessageProvider
import morestuff.shared.generated.resources.Res
import morestuff.shared.generated.resources.added_new_task
import morestuff.shared.generated.resources.can_i_do_for_you_today
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource

@OptIn(ExperimentalResourceApi::class)
class AppMessagesProviderImpl : AppMessageProvider {

    override fun getNewTaskAddedMessage(): StringResource {
        return Res.string.added_new_task
    }

    override fun getWhatCanIDoForYouMessage(): StringResource {
        return Res.string.can_i_do_for_you_today
    }
}

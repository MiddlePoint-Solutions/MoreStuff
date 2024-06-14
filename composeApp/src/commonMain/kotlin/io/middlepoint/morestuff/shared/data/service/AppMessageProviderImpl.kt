package io.middlepoint.morestuff.shared.data.service

import io.middlepoint.morestuff.shared.domain.service.AppMessageProvider
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.added_new_task
import morestuff.composeapp.generated.resources.can_i_do_for_you_today
import org.jetbrains.compose.resources.getString

class AppMessagesProviderImpl : AppMessageProvider {

  override suspend fun getNewTaskAddedMessage(): String {
    return getString(Res.string.added_new_task)
  }

  override suspend fun getWhatCanIDoForYouMessage(): String {
    return getString(Res.string.can_i_do_for_you_today)
  }
}

package io.middlepoint.morestuff.android.data.service

import android.content.Context
import android.content.res.Resources
import io.middlepoint.morestuff.android.R
import io.middlepoint.morestuff.android.domain.service.AppMessageProvider

class AppMessagesProviderImpl(context: Context) : AppMessageProvider {
    private val resources: Resources = context.resources

    override fun getNewTaskAddedMessage(): String {
        return resources.getString(R.string.added_new_task)
    }

    override fun getWhatCanIDoForYouMessage(): String {
        return resources.getString(R.string.can_i_do_for_you_today)
    }
}

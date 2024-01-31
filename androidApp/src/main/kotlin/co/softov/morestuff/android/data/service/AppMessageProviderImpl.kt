package co.softov.morestuff.android.data.service

import android.content.Context
import android.content.res.Resources
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.service.AppMessagesProvider

class AppMessagesProviderImpl(context: Context) : AppMessagesProvider {
    private val resources: Resources = context.resources

    override fun getNewTaskAddedMessage(): String {
        return resources.getString(R.string.added_new_task)
    }

    override fun getWhatCanIDoForYouMessage(): String {
        return resources.getString(R.string.can_i_do_for_you_today)
    }
}

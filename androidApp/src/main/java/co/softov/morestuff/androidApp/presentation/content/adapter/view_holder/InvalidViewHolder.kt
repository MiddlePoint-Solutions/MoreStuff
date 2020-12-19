package co.softov.morestuff.androidApp.presentation.content.adapter.view_holder

import android.view.View
import co.softov.morestuff.androidApp.domain.model.Message

class InvalidViewHolder(view: View) : ChatViewHolder(view) {

    override fun bind(item: Message) {
        // Ignore
    }
}
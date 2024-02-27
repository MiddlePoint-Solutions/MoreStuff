package co.softov.morestuff.android.ui.chat.task


import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import co.softov.morestuff.android.app.presentation.viewmodel.MoleculeViewModel
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class ChatPresenter(
    private val taskId: Long,
    private val savedState: SavedStateHandle
) : MoleculeViewModel<ChatEvent, ChatState>() {

    override val initialState: ChatState
        get() = savedState["Chat$taskId"] ?: ChatState()

    @Composable
    override fun models(events: Flow<ChatEvent>): ChatState {
        return chatModel(taskId, initialState, events)
    }

    override fun onSaveState(model: ChatState) {
        savedState["Chat$taskId"] = model
    }

}

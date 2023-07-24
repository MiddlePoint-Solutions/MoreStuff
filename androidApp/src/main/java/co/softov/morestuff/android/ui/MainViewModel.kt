package co.softov.morestuff.android.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.MessageAction
import co.softov.morestuff.android.domain.redux.store.OnResumeAction
import co.softov.morestuff.android.domain.usecase.settings.GetAppThemeUseCase
import co.softov.morestuff.android.ui.main.MainModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

class MainViewModel(
    getAppThemeUseCase: GetAppThemeUseCase,

) : NoStateViewModel() {

    var appTheme by mutableStateOf(getAppThemeUseCase())
        private set

    val model: MutableStateFlow<MainModel> = with(store.state.value.settings) {
        MutableStateFlow(
            MainModel(
                showOnBoarding = isFirstTime,
                theme = appTheme
            )
        )
    }

    init {
        loadData()
    }

    override fun onAppStateChange(state: AppState) {
        appTheme = state.settings.appTheme
        model.update {
            it.copy(
                showOnBoarding = state.settings.isFirstTime,
                theme = state.settings.appTheme
            )
        }
    }

    fun onResume() {
        store.dispatch(OnResumeAction)
    }

    fun shareTextToTask(taskId: Long, content: String) {
        store.dispatch(MessageAction.CreateUserTaskMessageAction(taskId, content))
    }

    fun shareContentToTask(taskId: Long, content: ShareableContent) {
        when (content) {
            is ShareableContent.TextContent -> {
                store.dispatch(MessageAction.CreateUserTaskMessageAction(taskId, content.content))
            }
            is ShareableContent.ImageContent -> {
                Timber.d("URI in shareContentToTask: ${content.uris}")
                store.dispatch(MessageAction.CreateImageMessageAction(taskId, content.uris, content.message))
            }
        }
    }


}

sealed class ShareableContent {
    data class TextContent(val content: String) : ShareableContent()
    data class ImageContent(val uris: String, val message: String) : ShareableContent()
}

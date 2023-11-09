package co.softov.morestuff.android.ui.input

import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.enums.Language
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.service.VoiceToTextParser
import co.softov.morestuff.android.ui.settings.SettingsModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class VoiceToTextViewModel(
    val voiceToTextParser: VoiceToTextParser
) : NoStateViewModel() {

    val model = MutableStateFlow(
        with(store.state.value.settings) {
            SettingsModel(
                inputVoiceLanguage = voiceInputLanguage
            )
        }
    )
    override fun onAppStateChange(state: AppState) {
        model.update {
            with(state.settings) {
                it.copy(
                    inputVoiceLanguage = voiceInputLanguage
                )
            }
        }
    }
    init {
        loadData()
    }

    fun startListening(languageCode: Language) {
        voiceToTextParser.startListening(languageCode)
    }

    fun stopListening() {
        voiceToTextParser.stopListening()
    }

}

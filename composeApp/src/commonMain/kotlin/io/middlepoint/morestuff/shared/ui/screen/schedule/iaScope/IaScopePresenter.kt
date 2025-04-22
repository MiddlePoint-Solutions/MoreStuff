package io.middlepoint.morestuff.shared.ui.screen.schedule.iaScope


import androidx.compose.runtime.Composable
import io.middlepoint.morestuff.shared.ui.MoleculeViewModel
import kotlinx.coroutines.flow.SharedFlow

import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.domain.service.logger

class IaScopePresenter(
    private val scopeId: Long,

) : MoleculeViewModel<IAChatEvent, IAChatState>() {

    override val initialState: IAChatState = IAChatState()

    init {
        logger.d { "IaScopePresenter inicializado para scopeId=$scopeId" }
    }

    @Composable
    override fun models(events: SharedFlow<IAChatEvent>): IAChatState {
        logger.d { "models() llamado para scopeId=$scopeId" }
        logger.d { "models() llamado para initial state=$initialState" }
        // Si quieres loggear cada evento recibido, puedes hacerlo dentro de iaChatModel
        return iaChatModel(scopeId, initialState, events, logger = logger)
    }
}

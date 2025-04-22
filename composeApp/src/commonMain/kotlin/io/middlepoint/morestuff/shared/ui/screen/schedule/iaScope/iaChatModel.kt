package io.middlepoint.morestuff.shared.ui.screen.schedule.iaScope

import androidx.compose.runtime.*
import co.touchlab.kermit.Logger
import io.github.vinceglb.filekit.name
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.middleware.IAMessageAction
import io.middlepoint.morestuff.shared.domain.usecase.ia.iaScope.GetScopeIAChatMessagesUseCase
import io.middlepoint.morestuff.shared.ui.model.map.IAMessageUiMapper

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.compose.koinInject

@Composable
fun iaChatModel(
    scopeId: Long,
    initialState: IAChatState,
    events: Flow<IAChatEvent>,
    store: AppStore = koinInject(),
    iaMessageUiMapper: IAMessageUiMapper = koinInject(),
    getScopeIAChatMessagesUseCase: GetScopeIAChatMessagesUseCase = koinInject(),
    logger: Logger = koinInject()
): IAChatState {
    var messages by remember { mutableStateOf(initialState.messages) }
    var isAIEnabled by remember { mutableStateOf(initialState.isAIEnabled) }
logger.d { "Comenzando con estado inicial de mensajes: ${messages.size} mensajes" }
    // Actualiza los mensajes en tiempo real
  /*  LaunchedEffect(scopeId) {
        logger.d { "LaunchedEffect: Observando mensajes para scopeId=$scopeId" }
        getScopeIAChatMessagesUseCase(scopeId).asFlow()
            .map { list ->
                logger.d { "Recibidos ${list.size} mensajes del use case para scopeId=$scopeId" }
                iaMessageUiMapper.map(list)
            }
            .collect { mappedMessages ->
                logger.d { "Actualizando estado de mensajes: ${mappedMessages.size} mensajes" }
                if (mappedMessages.isEmpty()) {
                    logger.w { "No hay mensajes para scopeId=$scopeId" }
                }
                messages = mappedMessages
            }
    }*/
    LaunchedEffect(Unit) {
        val messagesFlow = getScopeIAChatMessagesUseCase(scopeId).asFlow()
        messagesFlow
            .map { iaMessageUiMapper.map(it) }
            .collect { mappedMessages ->
                logger.d { "Actualizando mensajes IA: ${mappedMessages.size} mensajes para scopeId=$scopeId" }
                messages = mappedMessages
            }
    }

    LaunchedEffect(Unit) {
        logger.d { "LaunchedEffect: Observando eventos de chat IA" }
        events.collect { event ->
            logger.d { "Evento recibido: ${event::class.simpleName}" }
            when (event) {
                is IAChatEvent.InputText -> {
                    logger.d { "Dispatch: CreateScopeIAMessageAction y CreateScopeMessageByIAAction para '${event.content.trim()}'" }
                    store.dispatch(
                        IAMessageAction.CreateScopeIAMessageAction(
                            scopeId = scopeId,
                            content = event.content.trim(),
                        )
                    )
                    store.dispatch(
                        IAMessageAction.CreateScopeMessageByIAAction(
                            scopeId = scopeId,
                            prompt = event.content.trim()
                        )
                    )
                }
                is IAChatEvent.CreateAIMessage -> {
                    logger.d { "Dispatch: CreateScopeMessageByIAAction para prompt='${event.prompt}'" }
                    store.dispatch(
                        IAMessageAction.CreateScopeMessageByIAAction(
                            scopeId = scopeId,
                            prompt = event.prompt
                        )
                    )
                }
                is IAChatEvent.InputUserMedia -> {
                    logger.d { "Dispatch: CreateScopeIAMediaMessageAction para archivo='${event.imageFile.name}'" }
                    store.dispatch(
                        IAMessageAction.CreateScopeIAMediaMessageAction(
                            scopeId = scopeId,
                            contentType = ContentType.TASK_MESSAGE,
                            file = event.imageFile,
                            message = event.title.trim()
                        )
                    )
                }
                is IAChatEvent.InputDocument -> {
                    logger.d { "Evento InputDocument recibido (no implementado)" }
                    // Puedes crear una acción específica para PDF si la tienes
                }
                is IAChatEvent.CopyText -> {
                    logger.d { "Evento CopyText recibido (no implementado)" }
                    // Aquí puedes usar un helper de clipboard si lo tienes
                }
                is IAChatEvent.DeleteMessage -> {
                    logger.d { "Dispatch: DeleteScopeIAMessageAction para mensajeId=${event.message.id}" }
                    store.dispatch(
                        IAMessageAction.DeleteScopeIAMessageAction(event.message.id)
                    )
                }
                is IAChatEvent.OpenDocument -> {
                    logger.d { "Evento OpenDocument recibido (no implementado)" }
                    // Aquí puedes abrir el documento usando un handler si lo tienes
                }
                is IAChatEvent.ActivateAI -> {
                    isAIEnabled = !isAIEnabled
                    logger.d { "AI ${if (isAIEnabled) "enabled" else "disabled"} para scope: $scopeId" }
                }
            }
        }
    }

    // Log final del estado de mensajes
    LaunchedEffect(messages) {
        logger.d { "Estado final de mensajes actualizado: ${messages.size} mensajes" }
        if (messages.isEmpty()) {
            logger.w { "La lista de mensajes está vacía tras actualización para scopeId=$scopeId" }
        }
    }

    return IAChatState(
        messages = messages,
        isAIEnabled = isAIEnabled
    )
}
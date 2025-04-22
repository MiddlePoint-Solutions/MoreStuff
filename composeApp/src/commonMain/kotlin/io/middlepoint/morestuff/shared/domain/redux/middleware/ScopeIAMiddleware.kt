package io.middlepoint.morestuff.shared.domain.redux.middleware

import co.touchlab.kermit.Logger
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.redux.AppState
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import io.middlepoint.morestuff.shared.domain.redux.store.NoOp
import io.middlepoint.morestuff.shared.domain.usecase.ia.iaScope.CreateScopeIAMediaMessageUseCase
import io.middlepoint.morestuff.shared.domain.usecase.ia.iaScope.CreateScopeIAMessageUseCase
import io.middlepoint.morestuff.shared.domain.usecase.ia.iaScope.CreateScopeMessageByIAUseCase
import io.middlepoint.morestuff.shared.domain.usecase.ia.iaScope.DeleteScopeIAMessageUseCase
import io.middlepoint.morestuff.shared.domain.usecase.ia.iaScope.UpdateScopeIAMessageContentUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class IAMessageAction : Action.FeatureAction() {
    data class CreateScopeIAMessageAction(
        val scopeId: Long,
        val content: String,
    ) : IAMessageAction()

    data class CreateScopeIAMediaMessageAction(
        val scopeId: Long,
        val contentType: ContentType,
        val file: PlatformFile,
        val message: String
    ) : IAMessageAction()

    data class UpdateScopeIAMessageContentAction(
        val messageId: Long,
        val content: String
    ) : IAMessageAction()

    data class DeleteScopeIAMessageAction(
        val messageId: Long
    ) : IAMessageAction()

    data class CreateScopeMessageByIAAction(
        val scopeId: Long,
        val prompt: String
    ) : IAMessageAction()
}

val iaLogger = Logger.withTag("IAMessageMiddleware")

class ScopeIAMiddleware(
    private val createScopeIAMessageUseCase: CreateScopeIAMessageUseCase,
    private val createScopeIAMediaMessageUseCase: CreateScopeIAMediaMessageUseCase,
    private val updateScopeIAMessageContentUseCase: UpdateScopeIAMessageContentUseCase,
    private val deleteScopeIAMessageUseCase: DeleteScopeIAMessageUseCase,
    private val createScopeMessageByIAUseCase: CreateScopeMessageByIAUseCase,
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope,
    ): Action {
        when (action) {
            is IAMessageAction.CreateScopeIAMessageAction -> scope.launch {
                iaLogger.d { "Creating IA message in scope: ${action.scopeId}, content: ${action.content}" }
                createScopeIAMessageUseCase(
                    scopeId = action.scopeId,
                    content = action.content,
                    contentType = ContentType.TASK_MESSAGE,
                )
            }

            is IAMessageAction.CreateScopeMessageByIAAction -> scope.launch {
                iaLogger.d { "Creating IA scope message for prompt: ${action.prompt}" }
                createScopeMessageByIAUseCase(action.scopeId, action.prompt)
                iaLogger.d { "IA scope message created" }
            }

            is IAMessageAction.CreateScopeIAMediaMessageAction -> scope.launch {
                iaLogger.d { "Creating IA media message in scope: ${action.scopeId}, file: ${action.file.name}" }
                createScopeIAMediaMessageUseCase(
                    scopeId = action.scopeId,
                    contentType = ContentType.TASK_MESSAGE,
                    mediaFile = action.file,
                    message = action.message
                )
            }

            is IAMessageAction.UpdateScopeIAMessageContentAction -> scope.launch {
                iaLogger.d { "Updating IA message content: ${action.messageId}" }
                updateScopeIAMessageContentUseCase(
                    messageId = action.messageId,
                    content = action.content
                )
            }

            is IAMessageAction.DeleteScopeIAMessageAction -> scope.launch {
                iaLogger.d { "Deleting IA message: ${action.messageId}" }
                deleteScopeIAMessageUseCase(action.messageId)
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}
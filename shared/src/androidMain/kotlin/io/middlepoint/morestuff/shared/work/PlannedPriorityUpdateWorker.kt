package io.middlepoint.morestuff.shared.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.middleware.PriorityAction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlannedPriorityUpdateWorker(
    context: Context,
    params: WorkerParameters,
    ) : CoroutineWorker(context, params), KoinComponent {

    private val store: AppStore by inject()

    override suspend fun doWork(): Result {
        store.dispatchSuspend(PriorityAction.UpdatePlannedPriorityAction)
        return Result.success()
    }
}
package io.middlepoint.morestuff.android.app.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.middleware.PriorityAction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class PlannedPriorityUpdateWorker(
    context: Context,
    params: WorkerParameters,

    ) : CoroutineWorker(context, params), KoinComponent {

    private val store: AppStore by inject()

    override suspend fun doWork(): Result {
        Timber.d("Work: Dispatching UpdatePlannedTasksPriorityScore")
        store.dispatchSuspend(PriorityAction.UpdatePlannedPriorityAction)
        Timber.d("Work: Done")
        return Result.success()
    }
}
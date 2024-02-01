package co.softov.morestuff.android.ui.priority

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import co.softov.morestuff.android.domain.service.TimeManager
import org.koin.core.component.KoinComponent

class PlanViewModel(
    private val timeManager: TimeManager
) : ViewModel(), KoinComponent {

    var planTime = mutableStateOf(timeManager.getDefaultPlanTime())

    fun updatePlanTime() {
        planTime.value = timeManager.getDefaultPlanTime()
    }

}
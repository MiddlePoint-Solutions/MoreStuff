package co.softov.morestuff.android.presentation.model

import co.softov.morestuff.android.domain.model.DefaultOption
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PriorityOption

sealed interface PriorityModel {
    data class Today(val current: PriorityOption) : PriorityModel
    data class Tomorrow(val current: PriorityOption) : PriorityModel
    data class Later(val current: PriorityOption) : PriorityModel
}

fun Priority.mapToModel() = when (this) {
    is Priority.Later -> PriorityModel.Later(option)
    is Priority.Today -> PriorityModel.Today(option)
    is Priority.Tomorrow -> PriorityModel.Tomorrow(option)
}
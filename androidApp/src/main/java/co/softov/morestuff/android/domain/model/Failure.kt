package co.softov.morestuff.android.domain.model

sealed interface Failure

interface FeatureFailure : Failure

data class PriorityOptionsError(val message: String?) : FeatureFailure
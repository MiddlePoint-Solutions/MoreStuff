package co.softov.morestuff.android.domain.model

sealed interface Failure

interface FeatureFailure : Failure

data class PriorityOptionsError(val message: String?) : FeatureFailure

data class PrioritySchedulingNotAllowed(val priority: Priority) : FeatureFailure

data class TaskReorderFailure(val message: String?) : Failure

data class SaveImageFailure(val message: String?) : Failure
data class SavePdfFailure(val message: String?) : Failure

data class OpenGraphMetadataFetchFailure(val message: String?) : FeatureFailure

data object TaskReminderCancelled : FeatureFailure
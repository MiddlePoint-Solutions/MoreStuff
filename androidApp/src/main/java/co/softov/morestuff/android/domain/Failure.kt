package co.softov.morestuff.android.domain

sealed class Failure : Throwable() {
    abstract class FeatureFailure : Failure()
}
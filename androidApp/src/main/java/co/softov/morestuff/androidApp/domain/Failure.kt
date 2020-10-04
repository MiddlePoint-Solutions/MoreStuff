package co.softov.morestuff.androidApp.domain

sealed class Failure : Throwable() {
    abstract class FeatureFailure : Failure()
}
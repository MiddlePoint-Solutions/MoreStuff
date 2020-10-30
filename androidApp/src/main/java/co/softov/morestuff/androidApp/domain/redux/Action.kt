package co.softov.morestuff.androidApp.domain.redux

sealed class Action {
    abstract class FeatureAction : Action()
}

object NoAction : Action.FeatureAction()
object Init : Action.FeatureAction()
data class Test(val message: String) : Action.FeatureAction()
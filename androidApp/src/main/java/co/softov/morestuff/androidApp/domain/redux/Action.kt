package co.softov.morestuff.androidApp.domain.redux

sealed class Action {

    open val log: String
        get() = this.toString()

    abstract class FeatureAction : Action()
}

object NoOp : Action.FeatureAction()
object Init : Action.FeatureAction()
data class Test(val message: String) : Action.FeatureAction()
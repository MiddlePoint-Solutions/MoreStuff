package co.softov.morestuff.android.domain.redux.store

sealed class Action {

    open val log: String
        get() = this.toString()

    abstract class FeatureAction : Action()
}

object NoOp
object InitAction : Action.FeatureAction()
object OnResumeAction : Action.FeatureAction()
data class Test(val message: String) : Action.FeatureAction()
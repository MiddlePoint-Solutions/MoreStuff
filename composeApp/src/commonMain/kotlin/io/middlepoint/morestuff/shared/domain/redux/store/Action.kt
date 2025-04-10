package io.middlepoint.morestuff.shared.domain.redux.store

sealed class Action {

    open val log: String
        get() = this.toString()

    abstract class FeatureAction : Action()
}

data object NoOp
data object InitStoreAction : Action.FeatureAction()
data class Test(val message: String) : Action.FeatureAction()
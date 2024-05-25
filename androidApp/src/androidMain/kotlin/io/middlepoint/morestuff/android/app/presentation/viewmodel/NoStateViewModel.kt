package io.middlepoint.morestuff.android.app.presentation.viewmodel

object NoViewState : BaseViewState
object NoViewEvent : BaseViewEvent

open class NoStateViewModel : BaseViewModel<NoViewState, NoViewEvent>(NoViewState)
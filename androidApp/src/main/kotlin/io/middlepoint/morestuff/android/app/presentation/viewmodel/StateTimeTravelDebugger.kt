package io.middlepoint.morestuff.android.app.presentation.viewmodel

import timber.log.Timber
import kotlin.reflect.full.memberProperties

// Class logs ViewState transitions to facilitate debugging.
class StateTimeTravelDebugger(private val viewClassName: String) {

    private val stateTimeline = mutableListOf<StateTransition>()
    private var lastViewAction: BaseViewEvent? = null

    fun addEvent(viewAction: BaseViewEvent) {
        lastViewAction = viewAction
    }

    fun addStateTransition(oldState: BaseViewState, newState: BaseViewState) {
        val lastViewAction =
            checkNotNull(lastViewAction) { "lastViewAction is null. Please log action before logging state transition" }
        stateTimeline.add(
            StateTransition(
                oldState,
                lastViewAction,
                newState
            )
        )
        this.lastViewAction = null
    }

    private fun getMessage() = getMessage(stateTimeline)

    private fun getMessage(stateTimeline: List<StateTransition>): String {
        if (stateTimeline.isEmpty()) {
            return "$viewClassName has no state transitions \n"
        }

        var message = ""

        stateTimeline.forEach { stateTransition ->
            message += "Action: $viewClassName.${stateTransition.event.javaClass.simpleName}:\n"

            propertyNames.forEach { propertyName ->
                val logLine =
                    getLogLine(stateTransition.oldState, stateTransition.newState, propertyName)
                message += logLine
            }
        }

        return message
    }

    fun logAll() {
        Timber.d(getMessage())
    }

    fun logLast() {
        val states = listOf(stateTimeline.last())
        Timber.d(getMessage(states))
    }

    private fun getLogLine(
        oldState: BaseViewState,
        newState: BaseViewState,
        propertyName: String
    ): String {
        val oldValue = getPropertyValue(oldState, propertyName)
        val newValue = getPropertyValue(newState, propertyName)
        val indent = "\t"

        return if (oldValue != newValue) {
            "$indent*$propertyName: $oldValue -> $newValue\n"
        } else {
            "$indent$propertyName: $newValue\n"
        }
    }

    // Retrieve list of the properties from one of the ViewState instances (all have the same type)
    private val propertyNames by lazy {
        stateTimeline.first().oldState.javaClass.kotlin.memberProperties.map { it.name }
    }

    private fun getPropertyValue(baseViewState: BaseViewState, propertyName: String): String {
        baseViewState::class.memberProperties.forEach {
            if (propertyName == it.name) {
                var value =
                    if (it.returnType.classifier == List::class) {
                        val list = (it.getter.call(baseViewState) as List<*>)
                        if (list.size > 5)
                            "Showing: 5/${list.size} = ${list.subList(0, 5)}}"
                        else
                            list.toString()
                    } else {
                        it.getter.call(baseViewState).toString()
                    }

                if (value.isBlank()) {
                    value = "\"\""
                }

                return value
            }
        }
        return ""
    }

    private data class StateTransition(
        val oldState: BaseViewState,
        val event: BaseViewEvent,
        val newState: BaseViewState
    )
}

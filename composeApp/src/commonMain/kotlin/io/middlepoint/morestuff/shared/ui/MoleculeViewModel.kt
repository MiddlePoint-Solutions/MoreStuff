package io.middlepoint.morestuff.shared.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode.Immediate
import app.cash.molecule.moleculeFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

abstract class MoleculeViewModel<Event, Model> : ViewModel() {

  private val scope = CoroutineScope(viewModelScope.coroutineContext + Dispatchers.Main)

  protected abstract val initialState: Model

  // Events have a capacity large enough to handle simultaneous UI events, but
  // small enough to surface issues if they get backed up for some reason.
  private val events = MutableSharedFlow<Event>(extraBufferCapacity = 20)

  // TODO: the model is only active when state is collected. This also means that events will not be collected either.
  val models: StateFlow<Model> by lazy {
    moleculeFlow(mode = Immediate) {
      models(events)
    }.onEach(::onSaveState)
      .stateIn(scope, SharingStarted.Lazily, initialState)
  }

  fun take(event: Event) {
    if (!events.tryEmit(event)) {
      error("Event buffer overflow.")
    }
  }

  @Composable
  protected abstract fun models(events: SharedFlow<Event>): Model

  protected open fun onSaveState(model: Model) {}

}
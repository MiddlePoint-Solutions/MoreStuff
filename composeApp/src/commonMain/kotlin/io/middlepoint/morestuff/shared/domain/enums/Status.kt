package io.middlepoint.morestuff.shared.domain.enums

sealed class Status {
  data object Loading : Status()
  data object Ready: Status()
  data object Error: Status()
}

fun Status.isReady() = this == Status.Ready
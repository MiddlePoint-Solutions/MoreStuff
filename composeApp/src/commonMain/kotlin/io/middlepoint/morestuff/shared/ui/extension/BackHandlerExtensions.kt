package io.middlepoint.morestuff.shared.ui.extension

import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.backhandler.BackHandler

fun BackHandler.checkRegister(callback: BackCallback) {
  if (!isRegistered(callback)) {
    register(callback)
  }
}

fun BackHandler.checkUnregister(callback: BackCallback) {
  if (isRegistered(callback)) {
    unregister(callback)
  }
}
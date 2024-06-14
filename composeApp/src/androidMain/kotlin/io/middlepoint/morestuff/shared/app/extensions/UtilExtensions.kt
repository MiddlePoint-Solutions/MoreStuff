package io.middlepoint.morestuff.shared.app.extensions

val Any.simpleName: String
    get() = this::class.java.simpleName
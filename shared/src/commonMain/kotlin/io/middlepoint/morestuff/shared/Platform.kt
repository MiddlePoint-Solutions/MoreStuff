package io.middlepoint.morestuff.shared

expect class File(path: String) {
  fun exists(): Boolean
  fun delete(): Boolean
}

expect fun generateUUID(): String

expect abstract class Uri
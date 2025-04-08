package io.middlepoint.morestuff.shared.data.mapper

import io.github.jan.supabase.auth.user.UserInfo
import io.middlepoint.morestuff.shared.domain.model.User

typealias UserDataMapper = (
  userInfo: UserInfo
) -> User

fun makeUserDataMapper(): UserDataMapper = ::mapUserData

fun mapUserData(
  userInfo: UserInfo
): User = userInfo.run {
  User(
    id = id,
    fullName = userMetadata?.get("full_name")?.toString() ?: "",
    email = email,
    createdAt = createdAt,
    lastLogin = lastSignInAt
  )
}
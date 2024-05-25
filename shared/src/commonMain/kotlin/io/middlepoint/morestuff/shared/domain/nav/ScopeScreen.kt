package io.middlepoint.morestuff.shared.domain.nav

import io.middlepoint.morestuff.shared.domain.model.ScopeDomain
import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.serialization.Serializable

@Serializable
sealed class ScopeScreen {
  data object Root : ScopeScreen()
  data object Create : ScopeScreen()
  data class Edit(val scope: ScopeDomain) : ScopeScreen()
}
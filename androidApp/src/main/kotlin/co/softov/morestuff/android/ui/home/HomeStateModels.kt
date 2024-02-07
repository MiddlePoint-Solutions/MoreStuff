package co.softov.morestuff.android.ui.home

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.model.defaultScope
import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Immutable
data class HomeScopeState(
    val currentScopeId: Long = defaultScope.id,
    val scopes: List<ScopeDomain> = listOf()
): Parcelable


package io.middlepoint.morestuff.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import com.arkivanov.decompose.ComponentContext

@Composable
fun ProvideComponentContext(
    componentContext: ComponentContext,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalComponentContext provides componentContext, content = content)
}
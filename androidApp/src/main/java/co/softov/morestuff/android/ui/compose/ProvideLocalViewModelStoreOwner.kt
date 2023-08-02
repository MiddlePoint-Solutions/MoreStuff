package co.softov.morestuff.android.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import co.softov.morestuff.android.app.util.LifecycleViewModelStoreOwner

@Composable
fun ProvideLocalViewModelStoreOwner(
    lifecycleOwner: LifecycleOwner?,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalViewModelStoreOwner provides LifecycleViewModelStoreOwner(lifecycleOwner),
        content = content
    )
}
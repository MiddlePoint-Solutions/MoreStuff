package co.softov.morestuff.android.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun MoreStuffScaffold(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    showSettings: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    MoreStuffTheme {
        Scaffold(
            modifier = Modifier.systemBarsPadding(),
            scaffoldState = scaffoldState,
            topBar = { MoreStuffTopBar(showSettings) },
            content = content
        )
    }
}
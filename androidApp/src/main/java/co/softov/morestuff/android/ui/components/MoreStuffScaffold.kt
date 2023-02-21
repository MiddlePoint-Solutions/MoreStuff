package co.softov.morestuff.android.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.google.accompanist.insets.statusBarsPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreStuffScaffold(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    showSettings: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    MoreStuffTheme {
        Scaffold(
            modifier = Modifier.systemBarsPadding(),
            topBar = { MoreStuffTopBar(showSettings) },
            content = content
        )
    }
}
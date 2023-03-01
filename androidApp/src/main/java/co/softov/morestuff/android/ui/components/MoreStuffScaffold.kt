package co.softov.morestuff.android.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.DrawerValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import co.softov.morestuff.android.ui.Drawer.DrawerLayout
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreStuffScaffold(
    showSettings: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    MoreStuffTheme {
        val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
        val scope = rememberCoroutineScope()
        Scaffold(
            scaffoldState = scaffoldState,
            modifier = Modifier.systemBarsPadding(),
            topBar = { MoreStuffTopBar(showSettings,scope = scope, scaffoldState = scaffoldState) },
            content = content,
            drawerContent = {
                DrawerLayout()
            },
        )
    }
}
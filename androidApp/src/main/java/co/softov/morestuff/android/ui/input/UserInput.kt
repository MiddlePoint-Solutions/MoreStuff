package co.softov.morestuff.android.ui.input

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@Composable
fun UserInput(
    modifier: Modifier = Modifier,
    priorityContent: @Composable () -> Unit = {},
    textContent: @Composable () -> Unit = {},
) {
    Surface(
        modifier = modifier
            .imePadding()
            .zIndex(1f),
        color = Color.Transparent,
    ) {
        Column {
            priorityContent()
            textContent()
        }
    }
}

@Preview
@Composable
fun UserInputPreview() {
    MoreStuffTheme {
        UserInput()
    }
}

package co.softov.morestuff.android.ui.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@Composable
fun UserInput(
    modifier: Modifier = Modifier,
    priorityContent: @Composable () -> Unit = {},
    textContent: @Composable () -> Unit = {},
) {
    Column(modifier.imePadding()) {
        priorityContent()
        Surface(shadowElevation = 9.dp) {
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

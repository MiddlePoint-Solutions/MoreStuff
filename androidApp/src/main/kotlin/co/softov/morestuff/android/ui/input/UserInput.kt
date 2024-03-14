package co.softov.morestuff.android.ui.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@Composable
fun UserInput(
    modifier: Modifier = Modifier,
    priorityContent: @Composable () -> Unit = {},
    textContent: @Composable () -> Unit = {},
) {
    Column(
        modifier = modifier
            .imePadding()
            .navigationBarsPadding()
    ) {
        priorityContent()
        textContent()
    }
}

@Preview
@Composable
fun UserInputPreview() {
    MoreStuffTheme {
        UserInput()
    }
}

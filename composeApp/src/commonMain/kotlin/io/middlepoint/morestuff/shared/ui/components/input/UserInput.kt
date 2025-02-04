package io.middlepoint.morestuff.shared.ui.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun UserInput(
    modifier: Modifier = Modifier,
    priorityContent: @Composable () -> Unit = {},
    textContent: @Composable () -> Unit = {},
) {
    Column(
        modifier = modifier
            .imePadding()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        priorityContent()
        textContent()
    }
}

//@Preview
//@Composable
//fun UserInputPreview() {
//    MoreStuffTheme {
//        UserInput()
//    }
//}

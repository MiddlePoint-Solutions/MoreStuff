package io.middlepoint.morestuff.shared.ui.screen.scopes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun ScopeTitleEditor(
    title: String,
    onTitleChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    val fontSize = when (title.length) {
        in 0..9 -> 55.sp
        in 10..12 -> 50.sp
        else -> 50.sp
    }

    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.displayLarge.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = fontSize,
            textAlign = TextAlign.Center
        ),
        LocalContentColor provides MaterialTheme.colorScheme.onSurface
    ) {
        BasicTextField(
            value = title,
            onValueChange = {
                val input = it
                if (input.length <= 12) {
                    onTitleChange(input)
                }
            },
            modifier = modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Done
            ),
            textStyle = LocalTextStyle.current,
            singleLine = true,
            cursorBrush = SolidColor(LocalContentColor.current),
        ) { innerTextField ->
            if (title.isBlank()) {
                Text(
                    text = "Scope",
                    modifier = Modifier.fillMaxWidth(),
                    style = LocalTextStyle.current.copy(
                        color = LocalContentColor.current.copy(alpha = 0.4f),
                    )
                )
            }
            innerTextField()
        }
    }
}
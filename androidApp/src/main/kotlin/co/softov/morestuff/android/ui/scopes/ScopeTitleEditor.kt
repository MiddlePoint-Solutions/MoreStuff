package co.softov.morestuff.android.ui.scopes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ScopeTitleEditor(
    title: String,
    onTitleChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.displayLarge.copy(
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        ),
        LocalContentColor provides MaterialTheme.colorScheme.onSurface
    ) {
        BasicTextField(
            value = title,
            onValueChange = {
                val input = it.trim()
                if (input.length <= 9) {
                    onTitleChange(input)
                }
            },
            modifier = modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                autoCorrect = false,
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
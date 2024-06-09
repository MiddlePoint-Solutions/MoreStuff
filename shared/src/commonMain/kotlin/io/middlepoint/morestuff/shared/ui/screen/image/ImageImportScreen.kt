package io.middlepoint.morestuff.shared.ui.screen.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.ui.components.NavigateBackIconButton
import morestuff.shared.generated.resources.Res
import morestuff.shared.generated.resources.cd_send
import morestuff.shared.generated.resources.import_image_title
import morestuff.shared.generated.resources.import_image_title_hint
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageImportScreen(
    imageUri: String,
    onImport: (String) -> Unit,
    onBack: () -> Unit,
) {
    var messageText by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.import_image_title)) },
                navigationIcon = {
                    NavigateBackIconButton(onBack)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                )
            )
        },
        containerColor = Color.Transparent
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding(),
        ) {

//            Image(
//                painter = rememberAsyncImagePainter(model = imageUri), // TODO: check coil multiplatform
//                contentDescription = "Selected Image",
//                modifier = Modifier.weight(1f)
//            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = messageText,
                    onValueChange = { text -> messageText = text },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .defaultMinSize(minHeight = 46.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        if (messageText.isNotBlank()) {
                            onImport(messageText.trim())
                        }
                    }),
                    maxLines = Int.MAX_VALUE,
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 18.sp
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
                    decorationBox = { innerTextField ->
                        Box {
                            if (messageText.isEmpty()) {
                                Text(
                                    text = stringResource(Res.string.import_image_title_hint),
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        lineHeight = 20.sp,
                                        fontWeight = FontWeight(400),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        letterSpacing = 0.16.sp,
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                ImportConfirmButton(onClick = { onImport(messageText.trim()) })
            }
        }
    }
}

@Composable
private fun ImportConfirmButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(55.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Icon(
            imageVector = Icons.Default.Send,
            contentDescription = stringResource(Res.string.cd_send),
            modifier = Modifier
                .size(36.dp),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

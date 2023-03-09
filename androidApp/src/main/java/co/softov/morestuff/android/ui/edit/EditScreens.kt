package co.softov.morestuff.android.ui.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import timber.log.Timber

/*
 TODO: currently there is an issue with the keyboard appearing behind the bottom sheet
  See -> https://issuetracker.google.com/issues/268380384
  For now use EditFragment
*/
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun EditTaskTitleBottomSheet(
    openBottomSheet: Boolean,
    dismissAction: () -> Unit,
) {

    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberSheetState(skipHalfExpanded = false)
    if (openBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = dismissAction,
            sheetState = bottomSheetState,
        ) {
            EditTaskTitle(
                dismissAction = { /* Change task title */ },
                title = "Implement task title editing",
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskTitleAlertDialog(
    openBottomSheet: Boolean,
    dismissAction: () -> Unit,
) {

    val bottomSheetState = rememberSheetState(skipHalfExpanded = false)

    if (openBottomSheet) {
        AlertDialog(
            onDismissRequest = dismissAction,
        ) {
            EditTaskTitle(
                dismissAction = { /* Change task title */ },
                title = "Implement task title editing",
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditTaskTitle(
    dismissAction: () -> Unit,
    title: String,
) {

    val kc = LocalSoftwareKeyboardController.current

    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(title, TextRange(0, title.length)))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val focusRequester = remember { FocusRequester() }

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged {
                    Timber.d("FocusState changed: $it")
                }
        )

        Row {
            IconButton(onClick = {
                kc?.hide()
                dismissAction()
            }) {
                Icon(
                    imageVector = Icons.Filled.Cancel,
                    contentDescription = "Localized description"
                )
            }

            IconButton(onClick = dismissAction) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Localized description"
                )
            }
        }

        LaunchedEffect(key1 = Unit, block = {
            focusRequester.requestFocus()
        })

    }
}

@Preview
@Composable
fun EditTaskTitlePreview() {
    MoreStuffTheme(darkTheme = true) {
        EditTaskTitleBottomSheet(true, {})
    }
}
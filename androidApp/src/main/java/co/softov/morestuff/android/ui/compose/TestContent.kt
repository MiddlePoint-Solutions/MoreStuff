package co.softov.morestuff.android.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import co.softov.morestuff.android.ui.input.UserInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestContent(
    modifier: Modifier = Modifier
) {

    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var skipHalfExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()

// App content
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            Modifier.toggleable(
                value = skipHalfExpanded,
                role = Role.Checkbox,
                onValueChange = { checked -> skipHalfExpanded = checked }
            )
        ) {
            Checkbox(checked = skipHalfExpanded, onCheckedChange = null)
            Spacer(Modifier.width(16.dp))
            Text("Skip Half Expanded State")
        }
        Button(onClick = { openBottomSheet = !openBottomSheet }) {
            Text(text = "Show Bottom Sheet")
        }
    }

// Sheet content
    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    // Note: If you provide logic outside of onDismissRequest to remove the sheet,
                    // you must additionally handle intended state cleanup, if any.
                    onClick = {
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                openBottomSheet = false
                            }
                        }
                    }
                ) {
                    Text("Hide Bottom Sheet")
                }
            }

            UserInput(
                modifier = Modifier.imePadding(),
                showTaskListAction = { /*TODO*/ },
                onMessageSent = { }) {

            }
//            LazyColumn {
//                items(50) {
//                    ListItem(
//                        headlineContent = { Text("Item $it") },
//                        leadingContent = {
//                            Icon(
//                                Icons.Default.Favorite,
//                                contentDescription = "Localized description"
//                            )
//                        }
//                    )
//                }
//            }
        }
    }
}
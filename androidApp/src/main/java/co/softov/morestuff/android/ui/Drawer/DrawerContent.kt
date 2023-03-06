package co.softov.morestuff.android.ui.Drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.ui.Screens
import co.softov.morestuff.android.ui.theme.MoreStuffTheme


@Composable
fun DrawerLayout(
    onItemClicked: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        DrawerHeader()
        DrawerSettings("Settings") { onItemClicked(Screens.Settings.toString()) }
    }
}


@Composable
fun DrawerHeader() {
    Row(
        modifier = Modifier
            .padding(30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "More Stuff",
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.onSurface

        )
    }
}

@Composable
private fun DrawerSettings(text: String, onItemClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(CircleShape)
            .clickable(onClick = onItemClicked),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.Settings,
            contentDescription = "Settings",
            modifier = Modifier
                .padding(start = 16.dp).size(26.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Modifier
            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
            .size(24.dp)
        androidx.compose.material3.Text(
            text,
            fontSize = 26.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 15.dp)
        )
    }
}

@Composable
@Preview
fun DrawerPreview() {
    MoreStuffTheme {
        androidx.compose.material3.Surface {
            Column {
                DrawerLayout {}
            }
        }
    }
}

@Composable
@Preview
fun DrawerPreviewDark() {
    MoreStuffTheme(darkTheme = true) {
        androidx.compose.material3.Surface {
            Column {
                DrawerLayout {}
            }
        }
    }
}
package co.softov.morestuff.android.ui.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.PriorityHigh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun DrawerLayout(
    closeDrawer: () -> Unit = {},
    showSettings: () -> Unit = {},
    showPriorityReview: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        DrawerHeader()

        NavigationDrawerItem(
            label = {
                Text(
                    stringResource(R.string.settings),
                    fontSize = 26.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 15.dp)
                )
            },
            icon = {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.cd_settings),
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(26.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            },
            selected = false,
            onClick = {
                closeDrawer()
                showSettings()
            }
        )

        NavigationDrawerItem(
            label = {
                Text(
                    stringResource(R.string.priority_review),
                    fontSize = 26.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 15.dp)
                )
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.priority_48px),
                    contentDescription = stringResource(R.string.cd_priority_review),
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(26.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            },
            selected = false,
            onClick = {
                closeDrawer()
                showPriorityReview()
            }
        )
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
            text = "MoreStuff",
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
@Preview
fun DrawerPreview() {
    MoreStuffTheme {
        Surface {
            DrawerLayout(closeDrawer = {})
        }
    }
}

@Composable
@Preview
fun DrawerPreviewDark() {
    MoreStuffTheme(darkTheme = true) {
        Surface {
            DrawerLayout(closeDrawer = {})
        }
    }
}
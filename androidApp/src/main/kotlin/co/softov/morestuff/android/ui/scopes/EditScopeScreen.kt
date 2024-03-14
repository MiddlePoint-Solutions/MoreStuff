package co.softov.morestuff.android.ui.scopes

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ModeStandby
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.model.defaultScope
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.surfaceContainer
import co.softov.morestuff.android.ui.theme.surfaceContainerElevation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScopeScreen(
    scope: ScopeDomain,
    onBack: () -> Unit,
    onSaveScope: (String) -> Unit,
) {

    var scopeTitle by remember { mutableStateOf(scope.name) }
    var showSaveAction by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        snapshotFlow { scopeTitle }
            .distinctUntilChanged()
            .collectLatest { title ->
                showSaveAction = title != scope.name && title.isNotBlank()
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.title_edit_scope)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_back)
                        )
                    }
                },
                actions = {
                    if (showSaveAction) {
                        TextButton(
                            onClick = { onSaveScope(scopeTitle.trim()) },
                            contentPadding = PaddingValues()
                        ) {
                            Text(
                                text = stringResource(id = R.string.save).uppercase(),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerElevation
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {

            val (content, action) = createRefs()

            Column(
                modifier = Modifier.constrainAs(content) {
                    top.linkTo(parent.top)
                    bottom.linkTo(action.top)
                    height = Dimension.fillToConstraints
                    width = Dimension.matchParent
                },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Icon(
                    imageVector = Icons.Filled.ModeStandby,
                    contentDescription = stringResource(R.string.cd_scopes_icon),
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .size(60.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = stringResource(R.string.description_create_scope),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }

            Box(
                modifier = Modifier
                    .constrainAs(action) {
                        centerVerticallyTo(parent, bias = 0.4f)
                        width = Dimension.matchParent
                        height = Dimension.wrapContent
                    }
            ) {
                ScopeTitleEditor(
                    title = scopeTitle,
                    onTitleChange = { title -> scopeTitle = title.trim() },
                )
            }
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light"
)
@Composable
private fun Preview() {
    MoreStuffTheme {
        EditScopeScreen(
            scope = defaultScope,
            onBack = {},
            onSaveScope = {}
        )
    }
}
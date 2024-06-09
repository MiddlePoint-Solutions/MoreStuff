package io.middlepoint.morestuff.android.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.domain.model.ScopeDomain
import io.middlepoint.morestuff.shared.ui.compose.CustomScrollableTabRow
import io.middlepoint.morestuff.shared.ui.compose.TabRowDefaults.tabIndicatorOffset
import io.middlepoint.morestuff.android.ui.utils.containsEmoji

@Composable
fun ScopeTabs(
    currentPage: Int,
    scopes: List<ScopeDomain>,
    onScopeSelected: (index: Int, scope: ScopeDomain) -> Unit,
    containerColor: Color,
) {
    val selectedTabColor = MaterialTheme.colorScheme.primary
    val unselectedTabColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)

    CustomScrollableTabRow(
        selectedTabIndex = currentPage,
        minItemWidth = 18.dp,
        edgePadding = 18.dp,
        containerColor = containerColor,
        indicator = { tabPositions ->
            tabPositions.getOrNull(currentPage)?.let {
                CustomIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        currentTabPosition = it,
                    ),
                    color = selectedTabColor,
                    height = 4.dp,
                    cornerRadius = 12.dp
                )
            }
        },
        divider = {}
    ) {
        scopes.forEachIndexed { index, scope ->
            Tab(
                selected = index == currentPage,
                onClick = { onScopeSelected(index, scope) },
                modifier = Modifier.background(
                    color = containerColor,
                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                ),
                text = {

                    val size = remember(index, scope) {
                        if (scope.name.length == 1 && containsEmoji(scope.name)) {
                            18.sp
                        } else {
                            15.sp
                        }
                    }

                    Text(
                        text = scope.name,
                        style = TextStyle(
                            fontSize = size,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = if (index == currentPage) selectedTabColor else unselectedTabColor
                    )
                }
            )
        }
    }
}

@Composable
private fun CustomIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    width: Dp = 17.dp,
    height: Dp = 3.dp,
    cornerRadius: Dp = 10.dp,
) {
    Box(
        modifier
            .height(height)
            .clip(RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius))
            .background(color = color)
    )
}


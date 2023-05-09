package co.softov.morestuff.android.ui.priority

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PriorityOption
import co.softov.morestuff.android.domain.model.PriorityOptionsModel

@Composable
fun PriorityInput(
    model: Priority,
    onPriorityChange: (Priority) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        PrioritySelector(
            priority = model,
            onPrioritySelected = onPriorityChange
        )
    }
}

@Composable
fun UserPriorityOptionsInput(
    model: PriorityOptionsModel,
    onOptionSelected: (PriorityOption) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(5.dp)
    ) {

    }
}
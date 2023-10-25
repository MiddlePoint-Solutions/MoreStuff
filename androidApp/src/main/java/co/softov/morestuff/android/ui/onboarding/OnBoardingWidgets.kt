package co.softov.morestuff.android.ui.onboarding

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R

@Composable
fun OnboardingButton(
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onNext,
        modifier = modifier.fillMaxWidth(0.7f),
        content = {
            Text(
                text = stringResource(R.string.button_next),
                modifier = Modifier.padding(vertical = 4.dp),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        }
    )
}
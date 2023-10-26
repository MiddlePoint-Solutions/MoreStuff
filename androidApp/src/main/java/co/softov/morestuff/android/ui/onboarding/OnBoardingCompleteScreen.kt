package co.softov.morestuff.android.ui.onboarding

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@Composable
fun OnBoardingCompleteScreen(onFinish: () -> Unit) {
    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.safeContent)
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.your_workspace),
            modifier = Modifier.padding(top = 40.dp).align(Alignment.TopCenter),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 40.sp,
                lineHeight = 44.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            ),
            color = MaterialTheme.colorScheme.primary,

            )

        Image(
            painter = painterResource(id = R.drawable.onboarding_workspace),
            modifier = Modifier.align(Alignment.Center),
            contentDescription = "notifications",
            contentScale = ContentScale.FillBounds
        )

        OnboardingButton(
            onClick = onFinish,
            title = stringResource(id = R.string.button_ready),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        )
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
        OnBoardingCompleteScreen {}
    }
}

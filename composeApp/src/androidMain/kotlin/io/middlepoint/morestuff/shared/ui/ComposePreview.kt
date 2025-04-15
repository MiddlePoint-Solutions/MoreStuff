package io.middlepoint.morestuff.shared.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.middlepoint.morestuff.shared.ui.screen.onboarding.OnBoardingSignInContent
import io.middlepoint.morestuff.shared.ui.theme.MoreStuffTheme


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light",
)
@Composable
fun PriorityItemPreview() {
    MoreStuffTheme {
        OnBoardingSignInContent(
            signInWithGoogle = {},
            signInWithApple = {},
            onNext = {}
        )
    }
}

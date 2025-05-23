package io.middlepoint.morestuff.shared.ui.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.touchlab.kermit.Logger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.providers.Apple
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithApple
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.ui.ProviderButtonContent
import io.github.jan.supabase.compose.auth.ui.annotations.AuthUiExperimental
import io.middlepoint.morestuff.shared.Platform
import io.middlepoint.morestuff.shared.platform
import io.middlepoint.morestuff.shared.ui.theme.onBoardingBrush
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.button_skip
import morestuff.composeapp.generated.resources.ms_sign_in
import morestuff.composeapp.generated.resources.sign_in_with_email
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun SignInScreen(
  supabase: SupabaseClient = koinInject(),
  onNext: () -> Unit,
  onSignInWithEmail: () -> Unit
) {
  val signInWithGoogle = supabase.composeAuth.rememberSignInWithGoogle(
    onResult = { result ->
      Logger.d("Login: $result")
      when (result) {
        is NativeSignInResult.Success -> onNext()
        is NativeSignInResult.ClosedByUser -> {}
        is NativeSignInResult.Error -> {}
        is NativeSignInResult.NetworkError -> {}
      }
    }
  )

  val signInWithApple = supabase.composeAuth.rememberSignInWithApple(
    onResult = { result ->
      Logger.d("Login: $result")
      when (result) {
        is NativeSignInResult.Success -> onNext()
        is NativeSignInResult.ClosedByUser -> {}
        is NativeSignInResult.Error -> {}
        is NativeSignInResult.NetworkError -> {}
      }
    }
  )

  OnBoardingSignInContent(
    signInWithGoogle = { signInWithGoogle.startFlow() },
    signInWithApple = { signInWithApple.startFlow() },
    onSignInWithEmail = onSignInWithEmail
  )
}


@OptIn(AuthUiExperimental::class)
@Composable
fun OnBoardingSignInContent(
  signInWithGoogle: () -> Unit,
  signInWithApple: () -> Unit,
  onSignInWithEmail: () -> Unit
) {

  val background = remember { onBoardingBrush }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(background)
      .padding(horizontal = 10.dp)
  ) {

    Column(
      modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing)
        .padding(bottom = 100.dp),
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.weight(0.4f))

      Icon(
        painter = painterResource(Res.drawable.ms_sign_in),
        tint = Color.Unspecified,
        contentDescription = "signIn",
      )

      Spacer(modifier = Modifier.height(32.dp))

      Text(
        text = buildAnnotatedString {
          withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp)) {
            append("Organize & prioritize")
          }
          append("\n")
          withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 24.sp)) {
            append("your stuff")
          }
        },
        style = MaterialTheme.typography.headlineMedium.copy(
          color = Color.White
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )
      Spacer(modifier = Modifier.weight(1f))

      Column(horizontalAlignment = Alignment.CenterHorizontally) {

        if (platform == Platform.Android) {
          OutlinedButton(
            onClick = signInWithGoogle,
            modifier = Modifier
              .fillMaxWidth(0.8f)
              .height(56.dp),
            content = { ProviderButtonContent(Google, text = "Continue with Google") },
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = Color.White,
              containerColor = Color(0xFF393AC5)
            ),
            border = null,
          )
          Spacer(modifier = Modifier.height(16.dp))
        }

        if (platform == Platform.iOS) {
          OutlinedButton(
            onClick = signInWithApple,
            modifier = Modifier
              .fillMaxWidth(0.8f)
              .height(56.dp),
            content = { ProviderButtonContent(Apple, text = "Sign in with Apple") },
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = Color.White,
              containerColor = Color(0xFF393AC5)
            ),
            border = null,
          )
          Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedButton(
          onClick = onSignInWithEmail,
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White,
            containerColor = Color(0xFF393AC5)
          ),
          border = null,
          modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(56.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Email,
            contentDescription = stringResource(Res.string.sign_in_with_email),
            modifier = Modifier.padding(end = 8.dp)
          )
          Text(stringResource(Res.string.sign_in_with_email), fontSize = 12.sp)
        }
      }
    }
  }
}


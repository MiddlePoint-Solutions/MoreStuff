package io.middlepoint.morestuff.shared.ui.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.button_skip
import morestuff.composeapp.generated.resources.ms_sign_in
import morestuff.composeapp.generated.resources.sign_in_with_email
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun OnBoardingSignInScreen(
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
    onNext = onNext,
    onSignInWithEmail = onSignInWithEmail
  )
}

@OptIn(AuthUiExperimental::class)
@Composable
fun OnBoardingSignInContent(
  signInWithGoogle: () -> Unit,
  signInWithApple: () -> Unit,
  onNext: () -> Unit,
  onSignInWithEmail: () -> Unit
) {
  val gradientBrush = Brush.linearGradient(
    colors = listOf(Color(0xFF4C65FD), Color(0xFF4338D8)),
    start = Offset(0f, 0f),
    end = Offset(0f, Float.POSITIVE_INFINITY)
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(gradientBrush)
      .padding(horizontal = 10.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize(),
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(40.dp))

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
          painter = painterResource(Res.drawable.ms_sign_in),
          tint = Color.Unspecified,
          contentDescription = "signIn",
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
          text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
              append("Organize & prioritize")
            }
            append("\n")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
              append("your stuff")
            }
          },
          style = MaterialTheme.typography.headlineMedium.copy(
            color = Color.White
          ),
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth()
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedButton(
          onClick = signInWithGoogle,
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
          content = { ProviderButtonContent(Google, text = "Continue with Google") },
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White,
            containerColor = Color(0xFF393AC5)
          ),
          border = null,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
          onClick = signInWithApple,
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
          content = { ProviderButtonContent(Apple, text = "Sign in with Apple") },
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White,
            containerColor = Color(0xFF393AC5)
          ),
          border = null,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          OutlinedButton(
            onClick = onSignInWithEmail,
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = Color.White,
              containerColor = Color(0xFF393AC5)
            ),
            border = null,
            modifier = Modifier
              .weight(1f)
              .height(56.dp),
          ) {
            Icon(
              imageVector = Icons.Default.Email,
              contentDescription = stringResource(Res.string.sign_in_with_email),
              modifier = Modifier.padding(end = 8.dp)
            )
            Text(stringResource(Res.string.sign_in_with_email),fontSize = 12.sp)
          }

          Spacer(modifier = Modifier.width(16.dp))

          OutlinedButton(
            onClick = { /* TODO: wallet */ },
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = Color.White,
              containerColor = Color(0xFF393AC5)
            ),
            border = null,
            modifier = Modifier
              .weight(1f)
              .height(56.dp),
          ) {
            Icon(
              imageVector = Icons.Default.CurrencyBitcoin,
              contentDescription = "Wallet",
              modifier = Modifier.padding(end = 8.dp)
            )
            Text("Connect wallet", fontSize = 12.sp)
          }
        }
        Spacer(modifier = Modifier.height(34.dp))


       // Box(modifier = Modifier.height(43.dp)) {}
        TextButton(
          onClick = onNext,
          modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .height(43.dp),
          content = {
            Text(
              text = stringResource(Res.string.button_skip),
              style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
              )
            )
          }
        )
      }
    }
  }
}


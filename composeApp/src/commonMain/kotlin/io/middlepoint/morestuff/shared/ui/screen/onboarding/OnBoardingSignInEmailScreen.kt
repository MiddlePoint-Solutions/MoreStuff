package io.middlepoint.morestuff.shared.ui.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Label
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.middlepoint.morestuff.shared.ui.theme.onBoardingBrush
import io.middlepoint.morestuff.shared.ui.theme.onBoardingButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.`continue`
import morestuff.composeapp.generated.resources.email_label
import morestuff.composeapp.generated.resources.enter_code_sent_to_email
import morestuff.composeapp.generated.resources.enter_your_email
import morestuff.composeapp.generated.resources.error_generic
import morestuff.composeapp.generated.resources.error_invalid_email
import morestuff.composeapp.generated.resources.ms_sign_in
import morestuff.composeapp.generated.resources.resend_code
import morestuff.composeapp.generated.resources.seconds_to_resend
import morestuff.composeapp.generated.resources.six_digit_code_label
import morestuff.composeapp.generated.resources.verify
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun SignInEmailScreen(
  supabase: SupabaseClient = koinInject(),
  onNext: () -> Unit,
  logger: Logger = koinInject { parametersOf("EmailSignIn") }
) {
  val scope = rememberCoroutineScope()
  val invalidEmailError = stringResource(Res.string.error_invalid_email)
  val genericError = stringResource(Res.string.error_generic)

  var email by remember { mutableStateOf(TextFieldValue("")) }
  var emailError by remember { mutableStateOf<String?>(null) }
  var isEmailSent by remember { mutableStateOf(false) }
  var otpCode by remember { mutableStateOf("") }
  var isLoading by remember { mutableStateOf(false) }

  var canResendOtp by remember { mutableStateOf(false) }
  var secondsRemaining by remember { mutableStateOf(60) }
  var hasResentOtp by remember { mutableStateOf(false) }


  val sendOtpToEmail: (String, () -> Unit, (String) -> Unit) -> Unit = { email, onSent, onError ->
    scope.launch {
      isLoading = true
      try {
        supabase.auth.signInWith(OTP) {
          this.email = email
        }
        onSent()
        secondsRemaining = 60
        canResendOtp = false
      } catch (e: Exception) {
        logger.e(e) { "Error sending otp to email" }
        emailError = when {
          e.message?.contains(
            "email_address_invalid",
            ignoreCase = true
          ) == true -> invalidEmailError

          else -> genericError
        }
        onError(e.message ?: "Unknown error")
      } finally {
        isLoading = false
      }
    }
  }

  val verifyOtp: (String, String, () -> Unit) -> Unit = { email, code, onSuccess ->
    scope.launch {
      isLoading = true
      try {
        supabase.auth.verifyEmailOtp(
          type = OtpType.Email.EMAIL,
          email = email,
          token = code
        )
        onSuccess()
        onNext()
      } catch (e: Exception) {
        emailError = genericError
      } finally {
        isLoading = false
      }
    }
  }

  LaunchedEffect(isEmailSent) {
    if (isEmailSent && !hasResentOtp) {
      secondsRemaining = 60
      canResendOtp = false
      while (secondsRemaining > 0) {
        delay(1000)
        secondsRemaining--
      }
      canResendOtp = true
    }
  }

  val background = remember { onBoardingBrush }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(background)
      .imePadding()
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing)
        .padding(horizontal = 32.dp)
        .padding(bottom = 150.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Spacer(modifier = Modifier.weight(0.4f))
      Icon(
        painter = painterResource(Res.drawable.ms_sign_in),
        tint = Color.Unspecified,
        contentDescription = "signIn",
      )
      Spacer(modifier = Modifier.weight(0.6f))
      Text(
        text = if (!isEmailSent) stringResource(Res.string.enter_your_email)
        else stringResource(Res.string.enter_code_sent_to_email, email.text),
        style = MaterialTheme.typography.titleMedium,
        color = Color.White
      )
      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
        value = email,
        onValueChange = {
          if (!isEmailSent) {
            email = it
            emailError = null
          }
        },
        label = { Text(text = stringResource(Res.string.email_label)) },
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Email,
          imeAction = ImeAction.Done
        ),
        modifier = Modifier.fillMaxWidth(),
        isError = emailError != null,
        enabled = !isEmailSent,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = Color.White,
          unfocusedBorderColor = Color.White,
          cursorColor = Color.White,
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White,
          focusedLabelColor = Color.White,
          unfocusedLabelColor = Color.White,
        )
      )

      if (emailError != null) {
        Text(
          text = emailError ?: "",
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.padding(top = 8.dp)
        )
      }

      if (isEmailSent) {
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
          value = otpCode,
          onValueChange = { if (it.length <= 6) otpCode = it },
          label = {
            Text(
              text = stringResource(Res.string.six_digit_code_label),
              color = Color.White
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.VpnKey,
              contentDescription = null,
              tint = Color.White
            )
          },
          singleLine = true,
          shape = MaterialTheme.shapes.large,
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
          ),
          modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(8.dp))
        if (!hasResentOtp) {
          if (!canResendOtp) {
            Text(
              text = stringResource(Res.string.seconds_to_resend).replace(
                "%s",
                "$secondsRemaining"
              ),
              color = Color.White.copy(alpha = 0.7f),
              style = MaterialTheme.typography.bodySmall
            )
          } else {
            TextButton(
              onClick = {
                sendOtpToEmail(
                  email.text,
                  {
                    hasResentOtp = true
                    canResendOtp = false
                  },
                  {}
                )
              },
              enabled = !isLoading,
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
              ),
            ) {
              Text(
                text = stringResource(Res.string.resend_code)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      if (!isEmailSent) {
        OnBoardingButton(
          onClick = {
            sendOtpToEmail(
              email.text,
              { isEmailSent = true },
              {}
            )
          },
          enabled = isValidEmail(email.text) && !isLoading,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(text = stringResource(Res.string.`continue`))
        }
      } else {
        OnBoardingButton(
          onClick = {
            verifyOtp(
              email.text,
              otpCode
            ) { }
          },
          enabled = otpCode.length == 6 && !isLoading,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(text = stringResource(Res.string.verify))
        }
      }
    }
  }

}

private fun isValidEmail(email: String): Boolean {
  return Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    .matches(email)
}

@Preview
@Composable
fun SignInEmailScreenPreview() {
  SignInEmailScreen(onNext = {})
}
package io.middlepoint.morestuff.shared.ui.screen.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.OTP
import kotlinx.coroutines.launch
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.`continue`
import morestuff.composeapp.generated.resources.email_label
import morestuff.composeapp.generated.resources.enter_code_sent_to_email
import morestuff.composeapp.generated.resources.enter_your_email
import morestuff.composeapp.generated.resources.error_generic
import morestuff.composeapp.generated.resources.error_invalid_email
import morestuff.composeapp.generated.resources.six_digit_code_label
import morestuff.composeapp.generated.resources.verify
import org.jetbrains.compose.resources.stringResource
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

  val sendOtpToEmail: (String, () -> Unit, (String) -> Unit) -> Unit = { email, onSent, onError ->
    scope.launch {
      isLoading = true
      try {
        supabase.auth.signInWith(OTP) {
          this.email = email
        }
        onSent()
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

  Box(
    modifier = Modifier
      .fillMaxSize()
      .imePadding()
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = if (!isEmailSent) stringResource(Res.string.enter_your_email)
        else stringResource(Res.string.enter_code_sent_to_email, email.text),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
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
        label = { Text(stringResource(Res.string.email_label)) },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Email,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
          )
        },
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Email,
          imeAction = ImeAction.Done
        ),
        modifier = Modifier.fillMaxWidth(),
        isError = emailError != null,
        enabled = !isEmailSent
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
          label = { Text(stringResource(Res.string.six_digit_code_label)) },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.VpnKey,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary
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
      }

      Spacer(modifier = Modifier.height(24.dp))

      if (!isEmailSent) {
        Button(
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
          Text(stringResource(Res.string.`continue`))
        }
      } else {
        Button(
          onClick = {
            verifyOtp(
              email.text,
              otpCode
            ) { }
          },
          enabled = otpCode.length == 6 && !isLoading,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(stringResource(Res.string.verify))
        }
      }
    }
  }

}

fun isValidEmail(email: String): Boolean {
  return Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    .matches(email)
}
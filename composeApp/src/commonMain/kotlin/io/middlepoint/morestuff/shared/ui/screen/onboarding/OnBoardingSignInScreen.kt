package io.middlepoint.morestuff.shared.ui.screen.onboarding

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
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import co.touchlab.kermit.Logger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Apple
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithApple
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.ui.ProviderButtonContent
import io.github.jan.supabase.compose.auth.ui.annotations.AuthUiExperimental
import io.middlepoint.morestuff.shared.Platform
import io.middlepoint.morestuff.shared.platform
import io.middlepoint.morestuff.shared.ui.theme.surfaceContainerElevation
import kotlinx.coroutines.launch
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.button_enable
import morestuff.composeapp.generated.resources.button_skip
import morestuff.composeapp.generated.resources.`continue`
import morestuff.composeapp.generated.resources.email_label
import morestuff.composeapp.generated.resources.enter_code_sent_to_email
import morestuff.composeapp.generated.resources.enter_your_email
import morestuff.composeapp.generated.resources.error_generic
import morestuff.composeapp.generated.resources.error_invalid_email
import morestuff.composeapp.generated.resources.notification_permission_rationale
import morestuff.composeapp.generated.resources.onboarding_signin_subtitle
import morestuff.composeapp.generated.resources.onboarding_signin_title
import morestuff.composeapp.generated.resources.sign_in_with_email
import morestuff.composeapp.generated.resources.six_digit_code_label
import morestuff.composeapp.generated.resources.verify
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun OnBoardingSignInScreen(
  supabase: SupabaseClient = koinInject(),
  onNext: () -> Unit
) {
  val scope = rememberCoroutineScope()
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

  val sendOtpToEmail: (String, () -> Unit, (String) -> Unit) -> Unit = { email, onSent, onError ->
    scope.launch {
      try {
        supabase.auth.signInWith(OTP) {
          this.email = email
        }
        onSent()
      } catch (e: Exception) {
        Logger.d("OTP send failed: $e")
        onError(e.message ?: "Unknown error")
      }
    }
  }

  val verifyOtp: (String, String, () -> Unit) -> Unit = { email, code, onSuccess ->
    scope.launch {
      try {
        supabase.auth.verifyEmailOtp(
          type = OtpType.Email.EMAIL,
          email = email,
          token = code
        )
        onSuccess()
        onNext()
      } catch (e: Exception) {
        Logger.d("OTP verification failed: $e")
      }
    }
  }

  OnBoardingSignInContent(
    signInWithGoogle = { signInWithGoogle.startFlow() },
    signInWithApple = { signInWithApple.startFlow() },
    onSendOtpToEmail = sendOtpToEmail,
    onVerifyOtp = verifyOtp,
    onNext = onNext
  )
}

@OptIn(
  ExperimentalMaterial3Api::class, AuthUiExperimental::class
)
@Composable
fun OnBoardingSignInContent(
  signInWithGoogle: () -> Unit,
  signInWithApple: () -> Unit,
  onSendOtpToEmail: (String, () -> Unit, (String) -> Unit) -> Unit,
  onVerifyOtp: (String, String, () -> Unit) -> Unit,
  onNext: () -> Unit
) {
  var showRationaleDialog by remember { mutableStateOf(false) }
  var checkPermission by remember { mutableStateOf(false) }
  var showEmailSheet by remember { mutableStateOf(false) }
  var email by remember { mutableStateOf(TextFieldValue("")) }
  var emailError by remember { mutableStateOf<String?>(null) }
  var showOtpSheet by remember { mutableStateOf(false) }
  var otpEmail by remember { mutableStateOf("") }
  var otpCode by remember { mutableStateOf("") }
  val invalidEmailError = stringResource(Res.string.error_invalid_email)
  val genericError = stringResource(Res.string.error_generic)
  val scope = rememberCoroutineScope()

  if (showRationaleDialog) {
    BasicAlertDialog(
      onDismissRequest = { showRationaleDialog = false },
      properties = DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false
      )
    ) {
      Card {
        Text(
          text = stringResource(Res.string.notification_permission_rationale),
          modifier = Modifier.padding(10.dp)
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = {
            scope.launch { }
            showRationaleDialog = false
            checkPermission = true
          }) {
            Text(text = stringResource(Res.string.button_enable))
          }
          TextButton(onClick = {
            scope.launch { onNext() }
            showRationaleDialog = false
          }) {
            Text(text = stringResource(Res.string.button_skip))
          }
        }
      }
    }
  }

  Box(
    modifier = Modifier
      .windowInsetsPadding(WindowInsets.safeContent)
      .fillMaxSize()
  ) {
    ConstraintLayout(
      modifier = Modifier.fillMaxSize(),
    ) {
      val (image, title, subtitle) = createRefs()

      Text(
        text = stringResource(Res.string.onboarding_signin_title),
        modifier = Modifier
          .fillMaxWidth()
          .constrainAs(title) { top.linkTo(parent.top, margin = 40.dp) },
        style = MaterialTheme.typography.headlineMedium.copy(
          fontSize = 40.sp,
          lineHeight = 44.sp,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.Center
        ),
        color = MaterialTheme.colorScheme.primary,
      )
      Text(
        text = stringResource(Res.string.onboarding_signin_subtitle),
        modifier = Modifier
          .fillMaxWidth()
          .constrainAs(subtitle) { top.linkTo(title.bottom, margin = 20.dp) },
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.secondary,
      )
    }

    Column(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 40.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      OutlinedButton(
        onClick = signInWithGoogle,
        content = { ProviderButtonContent(Google) }
      )

      if (platform == Platform.iOS) {
        OutlinedButton(
          onClick = signInWithApple,
          content = { ProviderButtonContent(Apple) }
        )
      }

      OutlinedButton(
        onClick = { showEmailSheet = true }
      ) {
        Icon(
          imageVector = Icons.Default.Email,
          contentDescription = stringResource(Res.string.sign_in_with_email),
          modifier = Modifier.padding(end = 8.dp)
        )
        Text(stringResource(Res.string.sign_in_with_email))
      }

      Spacer(modifier = Modifier.padding(10.dp))

      TextButton(
        onClick = { onNext() },
        modifier = Modifier
          .width(187.dp)
          .height(43.dp),
        content = {
          Text(
            text = stringResource(Res.string.button_skip),
            style = TextStyle(
              fontSize = 16.sp,
              lineHeight = 28.sp,
              fontWeight = FontWeight(700),
              color = MaterialTheme.colorScheme.secondary,
            )
          )
        }
      )
    }
  }

  if (showEmailSheet) {
    EmailSignInBottomSheet(
      email = email,
      onEmailChange = {
        email = it
        emailError = null
      },
      onConfirm = {
        onSendOtpToEmail(
          email.text,
          {
            otpEmail = email.text
            showEmailSheet = false
            showOtpSheet = true
          },
          { errorMsg ->
            emailError = when {
              errorMsg.contains("email_address_invalid", ignoreCase = true) ->
                invalidEmailError

              else -> genericError

            }
          }
        )
      },
      onDismiss = { showEmailSheet = false },
      error = emailError
    )
  }
  if (showOtpSheet) {
    OtpVerificationBottomSheet(
      email = otpEmail,
      code = otpCode,
      onCodeChange = { otpCode = it },
      onConfirm = {
        onVerifyOtp(otpEmail, otpCode) {
          showOtpSheet = false
          otpCode = ""
        }
      },
      onDismiss = {
        showOtpSheet = false
        otpCode = ""
      }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailSignInBottomSheet(
  email: TextFieldValue,
  onEmailChange: (TextFieldValue) -> Unit,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit,
  error: String? = null
) {
  val isEmailValid = remember(email) { isValidEmail(email.text) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = MaterialTheme.colorScheme.surfaceContainerElevation,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        stringResource(Res.string.enter_your_email),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(12.dp))
      OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
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
        isError = error != null
      )
      if (error != null) {
        Text(
          text = error,
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.padding(top = 8.dp)
        )
      }
      Spacer(modifier = Modifier.height(20.dp))
      Button(
        onClick = onConfirm,
        enabled = isEmailValid,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(stringResource(Res.string.`continue`))
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationBottomSheet(
  email: String,
  code: String,
  onCodeChange: (String) -> Unit,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit
) {
  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = MaterialTheme.colorScheme.surfaceContainerElevation,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = stringResource(Res.string.enter_code_sent_to_email, email),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(12.dp))
      OutlinedTextField(
        value = code,
        onValueChange = onCodeChange,
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
      Spacer(modifier = Modifier.height(20.dp))
      Button(
        onClick = onConfirm,
        enabled = code.length == 6,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(stringResource(Res.string.verify))
      }
    }
  }
}

fun isValidEmail(email: String): Boolean {
  return Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    .matches(email)
}
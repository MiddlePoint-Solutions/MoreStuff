package io.middlepoint.morestuff.shared.ui.components.input.voice

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.domain.enums.Language
import io.middlepoint.morestuff.shared.ui.screen.settings.koinInjectOnRoute
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.cancel
import morestuff.composeapp.generated.resources.record_audio_permission_description
import morestuff.composeapp.generated.resources.record_audio_permission_title
import morestuff.composeapp.generated.resources.request_permission
import morestuff.composeapp.generated.resources.voice_to_text_listening
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun VoiceToTextInput(
    onUpdateValue: (String) -> Unit,
) {

    val viewModel = koinInjectOnRoute(VoiceToTextViewModel::class)
    val recordingState by viewModel.models.collectAsState()
    LaunchedEffect(recordingState.spokenText) {
        if (recordingState.spokenText.isNotEmpty()) {
            onUpdateValue(recordingState.spokenText)
            viewModel.take(VoiceToTextUiEvent.UpdateSpokenText(""))
        }
    }

    VoiceToTextInputContent(
        recordingState = recordingState,
        startListening = { viewModel.take(VoiceToTextUiEvent.StartListening) },
        stopListening = { viewModel.take(VoiceToTextUiEvent.StopListening) },
        selectedLanguage = displayLanguageName(viewModel)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VoiceToTextInputContent(
    recordingState: VoiceToTextState,
    startListening: () -> Unit,
    stopListening: () -> Unit,
    selectedLanguage: String,
) {

    // TODO: Support Audio recording permission

    var canRecord by remember { mutableStateOf(false) }
//    val recordAudioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(recordingState.isListening) {
        if (!recordingState.isListening) {
            stopListening()
            showDialog = false
        }
    }

    val onRecord = {
        if (!recordingState.isListening) {
            if (canRecord) {
                startListening()
                showDialog = true
            } else {
                showPermissionDialog = true
            }
        } else {
            stopListening()
            showDialog = false
        }
    }

    var dismissDialog by remember { mutableStateOf(false) }

    // TODO: Audio recording permission
//    if (recordAudioPermissionState.status is PermissionStatus.Denied && !dismissDialog && showPermissionDialog) {
//        MicrophonePermissionDialog(
//            onConfirm = {
//                recordAudioPermissionState.launchPermissionRequest()
//                dismissDialog = true
//
//            },
//            onCancel = { dismissDialog = true }
//        )
//    }
//
//    LaunchedEffect(recordAudioPermissionState.status) {
//        if (recordAudioPermissionState.status == PermissionStatus.Granted) {
//            canRecord = true
//        }
//    }

    Box(
        modifier = Modifier.height(IntrinsicSize.Min)
    ) {
        IconButton(onClick = onRecord) {
            Icon(
                imageVector = Icons.Filled.Mic,
                contentDescription = "",
            )
        }
    }

    LaunchedEffect(recordingState.isListening) {
        if (!recordingState.isListening) {
            showDialog = false
        }
    }

    if (showDialog) {
        BasicAlertDialog(onDismissRequest = {
            showDialog = false
            stopListening()
        }
        ) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier
                        .size(width = 200.dp, height = 250.dp)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    RecordingOverlay()

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stringResource(Res.string.voice_to_text_listening),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = selectedLanguage,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MicrophonePermissionDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(stringResource(Res.string.record_audio_permission_title)) },
        text = { Text(stringResource(Res.string.record_audio_permission_description)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(Res.string.request_permission))
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text(stringResource(Res.string.cancel))
            }
        },
        shape = RoundedCornerShape(4.dp),
    )
}


@Composable
fun RecordingOverlay() {
    val infiniteTransition = rememberInfiniteTransition(label = "Voice infinite transition")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "Voice scale"
    )
    val waveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "Voice alpha"
    )

    Box(
        modifier = Modifier
            .wrapContentSize()
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(100.dp)
                .scale(waveScale)
                .alpha(waveAlpha),
            strokeWidth = 4.dp,
            color = Color.White
        )
        CircularProgressIndicator(
            modifier = Modifier
                .size(100.dp)
                .scale(waveScale * 0.8f)
                .alpha(waveAlpha * 0.9f),
            strokeWidth = 4.dp,
        )

        Icon(
            imageVector = Icons.Filled.Mic,
            contentDescription = "Recording",
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.Center),
        )
    }
}

@Composable
fun displayLanguageName(viewModel: VoiceToTextViewModel): String {
    val state by viewModel.models.collectAsState()
    return if (state.detectedLanguage == Language.Device) {
        Locale.current.toLanguageTag()
    } else {
        state.detectedLanguage.name
    }
}

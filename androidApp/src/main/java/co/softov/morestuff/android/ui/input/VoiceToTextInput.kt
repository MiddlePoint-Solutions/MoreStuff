package co.softov.morestuff.android.ui.input

import android.Manifest
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.softov.morestuff.android.app.features.VoiceToTextParserState
import co.softov.morestuff.android.domain.service.VoiceToTextInterface
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.compose.koinInject

@Composable
fun VoiceToTextInput(
    onUpdateValue: (String) -> Unit,
    voiceToText: VoiceToTextInterface = koinInject()
) {

    val recordingState by voiceToText.state.collectAsStateWithLifecycle()

    VoiceToTextInputContent(
        onUpdateValue = onUpdateValue,
        startListening = { voiceToText.startListening("en") },
        stopListening = voiceToText::stopListening,
        recordingState = recordingState,
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun VoiceToTextInputContent(
    onUpdateValue: (String) -> Unit,
    startListening: () -> Unit,
    stopListening: () -> Unit,
    recordingState: VoiceToTextParserState = VoiceToTextParserState()
) {

    var canRecord by remember { mutableStateOf(false) }
    val recordAudioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    var showPermissionDialog by remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }

    if (recordingState.isSpeaking) {
        LaunchedEffect(Unit) {
            onUpdateValue(recordingState.spokenText)
        }
    }

    val onRecord = {
        if (!recordingState.isSpeaking) {
            if (canRecord) {
                startListening()
                showDialog.value = true
            } else {
                showPermissionDialog = true
            }
        } else {
            stopListening()
            showDialog.value = false
        }
    }

    var dismissDialog by remember { mutableStateOf(false) }

    if (recordAudioPermissionState.status is PermissionStatus.Denied && !dismissDialog && showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { dismissDialog = true },
            title = { Text("Record Audio Permission") },
            text = {
                Text(
                    "Recording audio is required for this feature to be available. " +
                            "Please grant the permission"
                )
            },
            confirmButton = {
                Button(onClick = {
                    recordAudioPermissionState.launchPermissionRequest()
                    dismissDialog = true
                }) {
                    Text("Request permission")
                }
            },
            dismissButton = {
                Button(onClick = { dismissDialog = true }) {
                    Text("Dismiss")
                }
            },
            shape = RoundedCornerShape(4.dp),
            backgroundColor = MaterialTheme.colorScheme.primary
        )
    }

    LaunchedEffect(recordAudioPermissionState.status) {
        if (recordAudioPermissionState.status == PermissionStatus.Granted) {
            canRecord = true
        }
    }

    Box(
        modifier = Modifier.height(IntrinsicSize.Min)
    ) {
        IconButton(onClick = onRecord) {
            AnimatedContent(
                targetState = recordingState.isSpeaking,
                label = "Voice recording animation"
            ) { isSpeaking ->
                if (isSpeaking) {
                    Icon(
                        imageVector = Icons.Filled.Stop,
                        contentDescription = "",
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = "",
                    )
                }
            }
        }
    }
    LaunchedEffect(recordingState.isSpeaking) {
        if (!recordingState.isSpeaking) {
            showDialog.value = false
        }
    }

    if (showDialog.value) {
        Dialog(onDismissRequest = {
            showDialog.value = false
            stopListening()
        }) {
            Card(shape = RectangleShape, modifier = Modifier.size(200.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    RecordingOverlay(isVisible = recordingState.isSpeaking)
                    Text(
                        text = "Listening",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun RecordingOverlay(isVisible: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "Voice infinite transition")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "Voice scale"
    )
    val waveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "Voice alpha"
    )

    if (isVisible) {
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
                color = Color.White
            )

            Surface(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Filled.Mic,
                    contentDescription = "Recording",
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Center),
                    tint = Color.White
                )
            }
        }
    }
}

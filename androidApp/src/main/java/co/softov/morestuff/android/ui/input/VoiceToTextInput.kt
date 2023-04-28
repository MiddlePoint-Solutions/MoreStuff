package co.softov.morestuff.android.ui.input

import android.Manifest
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.app.features.VoiceToTextInterface
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalAnimationApi::class, ExperimentalPermissionsApi::class)
@Composable
fun VoiceToTextInput(
    voiceToText: VoiceToTextInterface,
    onUpdateValue: (String) -> Unit,
) {
    var canRecord by remember { mutableStateOf(false) }
    val state by voiceToText.state.collectAsState()
    val recordAudioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val showPermissionDialog = remember { mutableStateOf(false) }

    LaunchedEffect(voiceToText.state) {
        voiceToText.state.collect { newState ->
            onUpdateValue(newState.spokenText)
        }
    }

    val onRecord = {
        if (!state.isSpeaking) {
            if (canRecord) {
                voiceToText.startListening("en")

            } else {
                showPermissionDialog.value = true
            }
        } else {
            voiceToText.stopListening()

        }
    }

    var dismissDialog by remember { mutableStateOf(false) }

    if (recordAudioPermissionState.status is PermissionStatus.Denied && !dismissDialog && showPermissionDialog.value) {
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
            }
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
            AnimatedContent(targetState = state.isSpeaking) { isSpeaking ->
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
}


@Composable
fun RecordingOverlay(isVisible: Boolean ) {
    val infiniteTransition = rememberInfiniteTransition()
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val waveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
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

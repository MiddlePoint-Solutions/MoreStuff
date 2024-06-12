package io.middlepoint.morestuff.shared.ui.screen.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.LibraryDefaults
import morestuff.shared.generated.resources.Res
import morestuff.shared.generated.resources.cd_navigate_back
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AboutLibrariesScreen(onBack: () -> Unit) {
  Scaffold(
    topBar = { AboutTopBar(onBack = onBack) }
  ) {

    var libraries by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
      libraries = Res.readBytes("files/aboutlibraries.json").decodeToString()
    }

    if(libraries.isNotEmpty()) {
      LibrariesContainer(
        aboutLibsJson = libraries,
        modifier = Modifier
          .fillMaxSize()
          .padding(it),
        colors = LibraryDefaults.libraryColors(
          backgroundColor = MaterialTheme.colorScheme.background,
          contentColor = contentColorFor(MaterialTheme.colorScheme.background),
          badgeBackgroundColor = MaterialTheme.colorScheme.secondary
        )
      )
    }

  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutTopBar(onBack: () -> Unit) {
  TopAppBar(
    title = {
      Text(
        text = "Open-Source Libraries",
        style = MaterialTheme.typography.titleLarge,
      )
    },
    navigationIcon = {
      IconButton(onClick = onBack) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = stringResource(Res.string.cd_navigate_back),
        )
      }
    },
  )
}
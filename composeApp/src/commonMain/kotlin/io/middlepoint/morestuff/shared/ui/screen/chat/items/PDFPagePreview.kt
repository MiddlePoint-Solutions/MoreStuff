package io.middlepoint.morestuff.shared.ui.screen.chat.items

import androidx.compose.runtime.Composable
import io.github.vinceglb.filekit.PlatformFile

@Composable
expect fun PDFPagePreview(
  pdfFile: PlatformFile,
  width: Int,
  height: Int,
  scale: Float = 0.38f,
)
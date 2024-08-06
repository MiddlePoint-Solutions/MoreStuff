package io.middlepoint.morestuff.shared.ui.screen.chat.items

import androidx.compose.runtime.Composable
import com.mohamedrejeb.calf.io.KmpFile

@Composable
expect fun PDFPagePreview(
  pdfFile: KmpFile,
  width: Int,
  height: Int,
  scale: Float = 0.38f,
)
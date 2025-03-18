package io.middlepoint.morestuff.shared.ui.screen.chat.items

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.calf.core.LocalPlatformContext
import io.middlepoint.morestuff.shared.createKmpFile
import io.middlepoint.morestuff.shared.ui.extension.getFileName
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.error
import morestuff.composeapp.generated.resources.filetype_pdf
import org.jetbrains.compose.resources.stringResource

@Composable
fun PDFMessageItem(
  pdfPath: String,
  message: MessageUiModel,
) {
  val pdfFile = remember(pdfPath) { createKmpFile(pdfPath) }
  val platformContext = LocalPlatformContext.current

    Box {
      Row(
        modifier = Modifier.fillMaxWidth()
      ) {
        BoxWithConstraints(modifier = Modifier.weight(0.8f)) {
          val width = with(LocalDensity.current) { maxWidth.toPx() }.toInt()

          PDFPagePreview(
            pdfFile = pdfFile,
            width = width,
            height = width,
          )
        }

        Column(
          modifier = Modifier
            .padding(8.dp)
            .weight(2f)
        ) {
          Text(
            text = pdfFile.getFileName(platformContext)
              ?: stringResource(Res.string.error),
            textAlign = TextAlign.Start,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge
          )

          Text(
            text = stringResource(Res.string.filetype_pdf),
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.outlineVariant
            )
          )
        }
      }

      MessageTime(
        formattedTimeOnly = message.formattedTimeOnly,
        modifier = Modifier.align(Alignment.BottomEnd)
      )
    }
}

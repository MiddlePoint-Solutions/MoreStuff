package io.middlepoint.morestuff.shared.ui.screen.chat.items

import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
private fun getFileNameFromUri(uri: Uri): String? {
  val context = LocalContext.current
  var fileName: String? = null

  when {
    uri.scheme.equals("content", ignoreCase = true) -> {
      val cursor = context.contentResolver.query(uri, null, null, null, null)
      cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex >= 0 && it.moveToFirst()) {
          fileName = it.getString(nameIndex)
        }
      }
    }
    uri.scheme.equals("file", ignoreCase = true) -> {
      fileName = uri.lastPathSegment
    }
  }
  return fileName
}


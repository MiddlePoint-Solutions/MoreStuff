package io.middlepoint.morestuff.shared.ui.utils

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.parseFragmentAndImportSession
import io.github.jan.supabase.auth.user.UserSession

@OptIn(SupabaseInternal::class)
suspend fun SupabaseClient.handleDeeplinkFragment(
  fragmentOrUrl: String,
  onSessionSuccess: (UserSession) -> Unit = {}
) {
  when (auth.config.flowType) {
    FlowType.IMPLICIT -> {
      auth.parseFragmentAndImportSession(fragmentOrUrl, onSessionSuccess)

    }

    FlowType.PKCE -> {
      val code = extractAccessToken(fragmentOrUrl) ?: return

      auth.exchangeCodeForSession(code)
      val session = auth.currentSessionOrNull() ?: error("No session available after code exchange")
      onSessionSuccess(session)
    }
  }
}

private fun extractQueryParam(url: String, param: String): String? {
  val queryPart = url.substringAfter('?', missingDelimiterValue = "")
  return queryPart
    .split('&').firstNotNullOfOrNull {
      val parts = it.split('=')
      if (parts.size == 2 && parts[0] == param) parts[1] else null
    }
}

private fun extractAccessToken(url: String): String? {
  val fragment = url.substringAfter('#', "")
  return extractQueryParam(fragment, "access_token")
}
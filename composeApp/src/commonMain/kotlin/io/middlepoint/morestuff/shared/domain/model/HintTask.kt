@file:OptIn(ExperimentalResourceApi::class)

package io.middlepoint.morestuff.shared.domain.model

import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource

data class HintTask(
  val taskTitle: StringResource,
  val taskMessages: List<StringResource>
)
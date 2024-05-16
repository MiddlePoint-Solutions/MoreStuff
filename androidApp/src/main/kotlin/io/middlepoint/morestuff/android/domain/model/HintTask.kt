package io.middlepoint.morestuff.android.domain.model

data class HintTask(val taskTitle: String, val taskMessages: List<HintMessage>)
data class HintMessage(val content: String)
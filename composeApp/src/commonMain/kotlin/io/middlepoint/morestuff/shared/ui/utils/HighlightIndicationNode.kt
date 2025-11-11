package io.middlepoint.morestuff.shared.ui.utils

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.invalidateDraw
import kotlinx.coroutines.launch

private class MyHighlightIndicationNode(private val interactionSource: InteractionSource) :
  Modifier.Node(), DrawModifierNode {
  private var isFocused = false

  override fun onAttach() {
    coroutineScope.launch {
      var focusCount = 0
      interactionSource.interactions.collect { interaction ->
        when (interaction) {
          is FocusInteraction.Focus -> focusCount++
          is FocusInteraction.Unfocus -> focusCount--
        }
        val focused = focusCount > 0
        if (isFocused != focused) {
          isFocused = focused
          invalidateDraw()
        }
      }
    }
  }

  override fun ContentDrawScope.draw() {
    drawContent()
    if (isFocused) {
      drawRect(size = size, color = Color.White, alpha = 0.2f)
    }
  }
}

object MyHighlightIndication : IndicationNodeFactory {
  override fun create(interactionSource: InteractionSource): DelegatableNode {
    return MyHighlightIndicationNode(interactionSource)
  }

  override fun hashCode(): Int = -1

  override fun equals(other: Any?) = other === this
}
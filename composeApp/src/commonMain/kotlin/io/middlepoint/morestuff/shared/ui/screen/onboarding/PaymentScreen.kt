package io.middlepoint.morestuff.shared.ui.screen.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import io.middlepoint.morestuff.shared.ui.theme.onBoardingBrush
import io.middlepoint.morestuff.shared.ui.theme.paymentBrush
import io.middlepoint.morestuff.shared.ui.theme.paymentOnSurface
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.ic_ai_enabled
import morestuff.composeapp.generated.resources.ic_ai_purple
import morestuff.composeapp.generated.resources.ic_cloud_upload
import morestuff.composeapp.generated.resources.ic_user
import org.jetbrains.compose.resources.painterResource

@Composable
fun PaymentScreen() {

  val onSurface = remember { paymentOnSurface }
  val gradientBrush = remember { paymentBrush }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.White)
      .padding(horizontal = 16.dp, vertical = 24.dp)
  ) {
    Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = "MoreStuff Pro",
          fontSize = 34.sp,
          fontWeight = FontWeight.Bold,
          color = onSurface,
          modifier = Modifier.padding(end = 8.dp)
        )
        Icon(
          painter = painterResource(Res.drawable.ic_ai_enabled),
          contentDescription = "AI",
          tint = Color.Unspecified,
          modifier = Modifier.size(34.dp)
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Make daily planning easy and enjoyable!",
        style = MaterialTheme.typography.titleLarge.copy(color = onSurface),
      )
      Spacer(modifier = Modifier.height(28.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        FeatureItem(
          icon = {
            Icon(
              painter = painterResource(Res.drawable.ic_ai_purple),
              contentDescription = "AI",
              tint = Color.Unspecified
            )
          },
          title = "AI assistant in every task",
          color = onSurface
        )

        FeatureItem(
          icon = {
            Icon(
              painter = painterResource(Res.drawable.ic_cloud_upload),
              contentDescription = "AI",
              tint = Color.Unspecified
            )
          },
          title = "Synchronization of data",
          color = onSurface
        )

        FeatureItem(
          icon = {
            Icon(
              painter = painterResource(Res.drawable.ic_user),
              contentDescription = "AI",
              tint = Color.Unspecified
            )
          },
          title = "Personalized recommendations",
          color = onSurface
        )
      }

      Spacer(modifier = Modifier.height(38.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        PaymentOptionCard(
          title = "1 month",
          price = "6,49 €",
          description = "billed monthly",
          onClick = { /*TODO*/ },
          background = Color(0xFFFFFFFF),
        )

        PaymentOptionCard(
          title = "12 month",
          price = "49,99 €",
          description = "billed annually",
          onClick = { /*TODO*/ },
          background = gradientBrush,
          border = BorderStroke(2.5.dp, gradientBrush),
          showSaveTag = true,
        )

      }


      Spacer(modifier = Modifier.height(38.dp))

      Button(
        onClick = { /*TODO*/ },
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8450F6))
      ) {
        Text(
          text = "Try 7 days free trial",
          fontSize = 16.sp,
          fontWeight = FontWeight.Medium,
          color = Color.White
        )
      }

      TextButton(
        onClick = { /*TODO*/ },
        modifier = Modifier.align(Alignment.CenterHorizontally),
        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF8450F6))
      ) {
        Text(
          text = "Maybe later",
          style = MaterialTheme.typography.labelLarge.copy(color = Color(0xFF8450F6)),
        )
      }

      Text(
        text = "By tapping Continue, you agree to subscribe to an auto-renewal Premium " +
            "plan via Apple. Unless cancelled at least 24-hours prior to the renewal date, " +
            "the subscription will be charged on that date. The plan's payment will be " +
            "charged every 12 months. By tapping Continue, you agree to our Privacy " +
            "Policy, Pricing Terms & Terms of Service. Subscriptions and auto-renewals " +
            "can be managed via Apple.",
        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF676363)),
      )
    }
  }
}

@Composable
fun FeatureItem(
  icon: @Composable () -> Unit,
  title: String,
  color: Color
) {
  val gradientBrush = Brush.linearGradient(
    colors = listOf(Color(0xFF4C65FD).copy(alpha = 0.14f), Color(0xFFB83EF0).copy(alpha = 0.12f)),
    start = Offset(0f, 0f),
    end = Offset(0f, Float.POSITIVE_INFINITY)
  )

  Card(
    modifier = Modifier
      .width(114.dp)
      .height(94.dp)
      .border(
        width = 1.dp,
        brush = gradientBrush,
        shape = RoundedCornerShape(20.dp)
      ),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(4.dp)
    ) {
      icon()
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
          color = color,
          fontSize = 11.sp,
          fontWeight = FontWeight.Normal
        ),
        modifier = Modifier.padding(top = 8.dp),
        textAlign = TextAlign.Center
      )
    }
  }
}


@Composable
fun PaymentOptionCard(
  title: String,
  price: String,
  description: String,
  onClick: () -> Unit,
  background: Any,
  border: BorderStroke = BorderStroke(0.dp, MaterialTheme.colorScheme.outline),
  elevation: Dp = 4.dp,
  showSaveTag: Boolean = false,
) {
  val shape = RoundedCornerShape(20.dp)

  val bgModifier = when (background) {
    is Color -> Modifier
      .clip(shape)
      .background(background)

    is Brush -> Modifier
      .clip(shape)
      .background(background)

    else -> Modifier
  }

  Box(
    modifier = Modifier
      .width(160.dp)
  ) {
    if (showSaveTag) {
      Box(
        modifier = Modifier
          .align(Alignment.TopCenter)
          .zIndex(1f)
          .offset(y = (-12).dp)
      ) {
        Box(
          modifier = Modifier
            //.height(24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF6C5CE7))
            .padding(horizontal = 12.dp, vertical = 4.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "save 15%",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    Box(
      modifier = Modifier
        .width(180.dp)
        .height(150.dp)
        //.shadow(elevation, shape)
        .then(bgModifier)
        .clip(shape)
        .border(border.width, border.brush, shape)
        .clickable(onClick = onClick),
      contentAlignment = Alignment.Center
    ) {
      Column(
        modifier = Modifier.align(Alignment.Center).padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = title,
          fontWeight = FontWeight.Medium,
          fontSize = 16.sp
        )
        Text(
          text = price,
          fontWeight = FontWeight.Bold,
          fontSize = 24.sp,
          modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
          text = description,
          color = Color.Gray,
          fontSize = 14.sp
        )
      }
    }
  }
}


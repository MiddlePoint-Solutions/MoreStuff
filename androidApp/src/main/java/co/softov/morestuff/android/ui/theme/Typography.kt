package co.softov.morestuff.android.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    bodyMedium = TextStyle(
        fontSize = 16.48.sp,
        lineHeight = 23.55.sp,
        fontWeight = FontWeight(500),
        textAlign = TextAlign.Center,
        letterSpacing = 0.16.sp,
    ),

    titleMedium = TextStyle(
        fontSize = 40.sp,
        lineHeight = 44.sp,
        fontWeight = FontWeight(900),
        textAlign = TextAlign.Center,
        letterSpacing = 0.40.sp,
    ),
    headlineLarge = TextStyle(
        fontSize = 60.sp,
        lineHeight = 88.sp,
        fontWeight = FontWeight(900),
        textAlign = TextAlign.Center,
        letterSpacing = 0.60.sp,
    ),


    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)
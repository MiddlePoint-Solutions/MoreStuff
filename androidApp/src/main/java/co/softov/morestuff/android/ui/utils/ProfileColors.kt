package co.softov.morestuff.android.ui.utils

import androidx.compose.ui.graphics.Color

object ProfileColors {

    fun getProfileColorsForTask(title: String) = profileColors[taskTitleToNumber(title)]

    private val profileColors = listOf(
        listOf(
            Color(98, 122, 246),
            Color(157, 100, 249),
        ),
        listOf(
            Color(255, 103, 103),
            Color(194, 69, 71)
        ),
        listOf(
            Color(229, 111, 248),
            Color(247, 155, 115)
        ),
        listOf(
            Color(60, 208, 36),
            Color(59, 217, 160)
        ),
        listOf(
            Color(240, 110, 50),
            Color(163, 100, 52)
        ),
        listOf(
            Color(20, 240, 240),
            Color(32, 190, 227)
        ),
        listOf(
            Color(130, 80, 200),
            Color(92, 166, 200)
        ),
        listOf(
            Color(90, 50, 255),
            Color(116, 42, 255)
        ),
        listOf(
            Color(220, 120, 40),
            Color(167, 34, 46)
        ),
        listOf(
            Color(130, 220, 100),
            Color(90, 230, 140)
        )
    )

    private fun taskTitleToNumber(str: String): Int {
        var sum = 0
        for (char in str) {
            sum += char.code
        }
        return sum % 10
    }

}


package co.softov.morestuff.android.ui.theme

import androidx.compose.ui.graphics.Color

object TaskColors {

    fun getProfileColorsForTask(title: String) = profileColors[taskTitleToNumber(title)]

    private val profileColors = listOf(
        listOf(
            Color(0xFFEDA185),
            Color(0xFFD86A41),
        ),
        listOf(
            Color(0xFFEE95FD),
            Color(0xFFC74BDB),
        ),
        listOf(
            Color(0xFFA676F4),
            Color(0xFF8853DF),
        ),
        listOf(
            Color(0xFF909BFB),
            Color(0xFF5967E3),
        ),
        listOf(
            Color(0xFF77C9F8),
            Color(0xFF2A85B9),
        ),
        listOf(
            Color(0xFF88F9BC),
            Color(0xFF23834F),
        ),
        listOf(
            Color(0xFF99F070),
            Color(0xFF448526),
        ),
        listOf(
            Color(0xFFFCFF68),
            Color(0xFF797B00),
        ),
        listOf(
            Color(0xFFD2D2D2),
            Color(0xFF6C6C6C),
        ),
        listOf(
            Color(0xFFDA496C),
            Color(0xFF7D293D),
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


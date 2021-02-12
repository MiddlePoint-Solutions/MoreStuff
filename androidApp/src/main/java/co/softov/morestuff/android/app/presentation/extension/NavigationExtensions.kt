package co.softov.morestuff.android.app.presentation.extension

import com.github.terrakok.cicerone.BackTo
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.Replace
import com.github.terrakok.cicerone.androidx.AppScreen

fun Navigator.setLaunchScreen(screen: AppScreen) {
    applyCommands(
        arrayOf(
            BackTo(null),
            Replace(screen)
        )
    )
}
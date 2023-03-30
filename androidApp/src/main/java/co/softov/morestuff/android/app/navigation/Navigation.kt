package co.softov.morestuff.android.app.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.Creator

/**
 *  Custom router.
 */
open class AppRouter : Router() {

    fun showBottomSheet(screen: Screen) {
        executeCommands(MsNavCommand.ShowBottomSheet(screen))
    }

}

/**
 * Custom router navigation commands.
 */
sealed class MsNavCommand : Command {

    /**
     * Display a bottom sheet dialog.
     */
    data class ShowBottomSheet(val screen: Screen, val isModal: Boolean = false) : MsNavCommand()

}

interface BottomSheetFragmentScreen : Screen {
    fun createFragment(factory: FragmentFactory): Fragment

    companion object {
        operator fun invoke(
            key: String? = null,
            fragmentCreator: Creator<FragmentFactory, Fragment>
        ) = object : BottomSheetFragmentScreen {
            override val screenKey = key ?: fragmentCreator::class.java.name
            override fun createFragment(factory: FragmentFactory) = fragmentCreator.create(factory)
        }
    }
}
package co.softov.morestuff.android.app.navigation

import androidx.fragment.app.*
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.navigation.MsNavCommand.ShowBottomSheet
import co.softov.morestuff.android.ui.ScreenKey
import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber

open class MoreStuffNavigator @JvmOverloads constructor(
    activity: FragmentActivity,
    containerId: Int,
    fragmentManager: FragmentManager = activity.supportFragmentManager,
    fragmentFactory: FragmentFactory = fragmentManager.fragmentFactory
) : AppNavigator(activity, containerId, fragmentManager, fragmentFactory) {

    override fun applyCommand(command: Command) {
        when (command) {
            is ShowBottomSheet -> showBottomSheet(command)
            else -> super.applyCommand(command)
        }
    }

    private fun showBottomSheet(command: ShowBottomSheet) {
        when (val screen = command.screen) {
            is BottomSheetFragmentScreen -> {
                val dialog = screen.createFragment(fragmentFactory) as BottomSheetDialogFragment
                Timber.d("Showing modal bottom sheet: ${screen.screenKey}")
                dialog.show(fragmentManager, command.screen.screenKey)
            }
            else -> Timber.e("${screen.screenKey} dialog should extend FragmentScreen")
        }
    }

    override fun setupFragmentTransaction(
        screen: FragmentScreen,
        fragmentTransaction: FragmentTransaction,
        currentFragment: Fragment?,
        nextFragment: Fragment
    ) {
        when {
            screen.screenKey.contains(ScreenKey.LAUNCHED_TASK_CHAT) -> {
                fragmentTransaction.setCustomAnimations(
                    0,
                    R.anim.slide_out_right,
                    0,
                    R.anim.slide_out_right
                )
            }
//                    screen.screenKey.contains(ScreenKey.TASK_CHAT) -> {
            else -> {
                fragmentTransaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_right,
                    R.anim.slide_in_right,
                    R.anim.slide_out_right
                )
            }
        }
    }
}
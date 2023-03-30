package co.softov.morestuff.android.ui.drawer

import androidx.lifecycle.ViewModel
import co.softov.morestuff.android.ui.Screens
import co.softov.morestuff.android.app.navigation.AppRouter
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import com.github.terrakok.cicerone.Router
import org.koin.core.component.KoinComponent

class DrawerViewModel : NoStateViewModel() {

    fun showSettings() {
        router.navigateTo(Screens.ComposeSettings)
    }

    fun showPriorityReview() {
        router.navigateTo(Screens.priorityReview)
    }

}
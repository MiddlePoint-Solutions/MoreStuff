package co.softov.morestuff.android.ui.drawer

import androidx.lifecycle.ViewModel
import co.softov.morestuff.android.ui.Screens
import com.github.terrakok.cicerone.Router
import org.koin.core.component.KoinComponent

class DrawerViewModel(
    private val router: Router
) : ViewModel(), KoinComponent {

    fun showSettings() {
        router.navigateTo(Screens.ComposeSettings)
    }

    fun showPriorityReview() {
        router.navigateTo(Screens.priorityReview)
    }

}
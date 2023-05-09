package co.softov.morestuff.android.ui.drawer

import co.softov.morestuff.android.ui.Screens
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel

class DrawerViewModel : NoStateViewModel() {

    fun showSettings() {
        router.navigateTo(Screens.ComposeSettings)
    }

    fun showReview() {
        router.navigateTo(Screens.review)
    }

}
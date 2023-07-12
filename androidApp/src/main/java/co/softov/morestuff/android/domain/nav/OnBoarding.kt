package co.softov.morestuff.android.domain.nav

import android.os.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize


sealed class OnBoarding : Parcelable {

    @Parcelize
    object Welcome : OnBoarding()

    @Parcelize
    object NotificationPermission : OnBoarding()
}


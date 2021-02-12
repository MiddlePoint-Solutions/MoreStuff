package co.softov.morestuff.android.presentation

import co.softov.morestuff.android.feature.review.ReviewFragment
import co.softov.morestuff.android.presentation.content.ContentFragment
import co.softov.morestuff.android.presentation.settings.MainSettings
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {

    val Content = FragmentScreen("Content") { ContentFragment() }

    val Settings = FragmentScreen("Settings") { MainSettings() }

    val Review = FragmentScreen("Review") { ReviewFragment() }

}
package co.softov.morestuff.androidApp.presentation.content

import android.os.Bundle
import android.view.View
import co.softov.morestuff.android.R
import co.softov.morestuff.androidApp.MainActivity
import co.softov.morestuff.androidApp.app.presentation.fragment.FlowFragment
import co.softov.morestuff.androidApp.presentation.Screens
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.androidx.AppScreen

class ContentFlowFragment(holder: NavigatorHolder) : FlowFragment(holder) {

    override val layoutResourceId: Int = R.layout.activity_main

    override val mainContainerId: Int = R.id.main_content

    override fun getLaunchScreen(): AppScreen = Screens.Content

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setSupportActionBar(view.findViewById(R.id.my_toolbar))
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}
package co.softov.morestuff.android.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberDrawerState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.ListFragment
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.presentation.fragment.BaseFragment
import co.softov.morestuff.android.app.util.anyLog
import co.softov.morestuff.android.ui.Screens
import co.softov.morestuff.android.ui.components.MoreStuffScaffold
import co.softov.morestuff.android.ui.compose.viewMigration
import co.softov.morestuff.android.ui.list.ListsFragment
import com.github.terrakok.cicerone.Router
import org.koin.android.ext.android.inject

class MainFragment : BaseFragment() {

    private val conductor = object : MainConductor {

        override fun showTaskList() {
            ListsFragment().show(parentFragmentManager, ListFragment::javaClass.name)
        }
    }

    private val router: Router by inject()

    override val layoutResourceId: Int
        get() = R.layout.fragment_content

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        anyLog("onCreateView")
        return ComposeView(inflater.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            viewMigration {
                MoreStuffScaffold(
                    drawerState = DrawerState(initialValue = DrawerValue.Closed),
                    scaffoldState = ScaffoldState(
                        rememberDrawerState(initialValue = androidx.compose.material.DrawerValue.Closed),
                        snackbarHostState = SnackbarHostState()
                    ),
                    content = {
                        MainContent(conductor)
                    },
                    scope = rememberCoroutineScope(),
                    onItemClicked = {},
                )
            }
        }
    }

    override fun onBackPressed() {
        //viewModel.onBackPressed()
    }

    private fun showMainSettings() {
        router.navigateTo(Screens.Settings)
    }
}


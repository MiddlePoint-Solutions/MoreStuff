package co.softov.morestuff.android.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        savedInstanceState: Bundle?
    ): View {
        anyLog("onCreateView")
        return ComposeView(inflater.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            viewMigration {
                MoreStuffScaffold(showSettings = ::showMainSettings) {
                    MainContent(conductor)
                }
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


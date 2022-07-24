package co.softov.morestuff.android.ui.main

import android.os.Bundle
import android.view.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.ListFragment
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.presentation.fragment.BaseFragment
import co.softov.morestuff.android.ui.Screens
import co.softov.morestuff.android.ui.components.MoreStuffScaffold
import co.softov.morestuff.android.ui.compose.viewMigration
import co.softov.morestuff.android.ui.dashboard.options.DatePickerFragment
import co.softov.morestuff.android.ui.list.ListsFragment
import com.github.terrakok.cicerone.Router
import org.koin.android.ext.android.inject
import java.util.*

class ContentFragment : BaseFragment() {

    private val conductor = object : ContentConductor {

        override fun showTaskList() {
            listFragment.show(parentFragmentManager, ListFragment::javaClass.name)
        }

        override fun showTodayTimePicker() {
            showTimePicker()
        }

        override fun showTomorrowTimePicker() {
            val tomorrowTime =
                Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis
            showTimePicker(tomorrowTime)
        }

        override fun showDateTimePicker() {
            DatePickerFragment { userTime ->
                showTimePicker(userTime)
            }.show(parentFragmentManager, "DatePicker")
        }

        private fun showTimePicker(withTime: Long = 0) {
            /*TimePickerFragment.createInstance(withTime) { time ->
                viewModel.userSetCustomTime(time)
            }.show(parentFragmentManager, "TimePicker")*/
        }
    }

    private val listFragment: ListsFragment get() = ListsFragment()
    private val router: Router by inject()

    override val layoutResourceId: Int
        get() = R.layout.fragment_content

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(inflater.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            viewMigration {
                MoreStuffScaffold(showSettings = ::showMainSettings) {
                    ChatContent(conductor)
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


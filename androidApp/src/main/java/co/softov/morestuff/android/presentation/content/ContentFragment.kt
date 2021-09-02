package co.softov.morestuff.android.presentation.content

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.iterator
import androidx.fragment.app.ListFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.presentation.extension.observe
import co.softov.morestuff.android.app.presentation.fragment.BaseFragment
import co.softov.morestuff.android.app.util.LifecycleValue
import co.softov.morestuff.android.databinding.FragmentContentBinding
import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.enums.Priority.*
import co.softov.morestuff.android.presentation.Screens
import co.softov.morestuff.android.presentation.compose.viewMigration
import co.softov.morestuff.android.presentation.content.adapter.ChatAdapter
import co.softov.morestuff.android.presentation.content.adapter.view_holder.ChatViewHolderFactory
import co.softov.morestuff.android.presentation.dashboard.options.DatePickerFragment
import co.softov.morestuff.android.presentation.list.ListsFragment
import co.softov.morestuff.android.presentation.theme.MoreStuffTheme
import com.github.terrakok.cicerone.Router
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class ContentFragment : BaseFragment() {

    private val viewModel: ContentViewModel by viewModel {
        parametersOf(conductor)
    }

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
    private var binding: FragmentContentBinding by LifecycleValue()
    private var chatAdapter: ChatAdapter by LifecycleValue()

    override val layoutResourceId: Int
        get() = R.layout.fragment_content

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentContentBinding.bind(view)
        setHasOptionsMenu(true)
        initViews()
        viewModel.loadData()
        observe(viewModel.uiState, ::onStateChange)
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    private fun initViews() {
        setupChatList()
        setupChatInput()
        setupPriorities()
    }

    private fun setupPriorities() {
        binding.composePriorityConfig.viewMigration {
            val state by viewModel.uiState.collectAsState()

            UserPriorityInput(
                currentPriority = state.priority
            ) {
                when (it) {
                    is Later -> viewModel.selectedLaterPriority()
                    is Today -> viewModel.selectedTodayPriority()
                    is Tomorrow -> viewModel.selectedTomorrowPriority()
                }
            }
        }
    }



    private fun setupChatInput() {
        binding.composeChatInput.viewMigration {
            UserTextInput(
                listAction = viewModel::showTaskList,
                sendAction = viewModel::addNewTask
            )
        }
    }

    private fun setupChatList() {
        val actions = ChatActions(
            confirmationAction = viewModel::userSelectedPriorityOption,
            scheduleAction = viewModel::scheduleResponse
        )

        val factory = ChatViewHolderFactory(actions)

        chatAdapter = ChatAdapter(factory)
        chatAdapter.setHasStableIds(true)
        chatAdapter.registerAdapterDataObserver(AdapterDataObserver())

        binding.apply {
            recyclerChatMessages.apply {
                adapter = chatAdapter
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = true
            }

            val layoutManager = LinearLayoutManager(recyclerChatMessages.context)
            layoutManager.stackFromEnd = false
            layoutManager.reverseLayout = true
            recyclerChatMessages.layoutManager = layoutManager
        }
    }

    private fun onStateChange(state: ContentViewState) {
        state.data?.let {
            chatAdapter.submitList(it)
        }
    }

    inner class AdapterDataObserver : RecyclerView.AdapterDataObserver() {

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            if (positionStart == 0) {
                scrollToCurrentMessage()
            }
        }

        private fun scrollToCurrentMessage() {
            binding.recyclerChatMessages.scrollToPosition(0)
        }
    }

    private fun showMainSettings() {
        router.navigateTo(Screens.Settings, clearContainer = false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.iterator().forEach { it.isVisible = true }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        closeKeyboard()
        when (item.itemId) {
            R.id.option_settings -> {
                showMainSettings()
            }
        }
        return false
    }
}
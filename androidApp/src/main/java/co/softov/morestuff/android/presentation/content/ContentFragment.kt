package co.softov.morestuff.android.presentation.content

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.iterator
import androidx.fragment.app.ListFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import co.softov.morestuff.android.R
import co.softov.morestuff.android.databinding.FragmentContentBinding
import co.softov.morestuff.android.app.presentation.extension.observe
import co.softov.morestuff.android.app.presentation.extension.setOnDebouncedClickListener
import co.softov.morestuff.android.app.presentation.fragment.BaseFragment
import co.softov.morestuff.android.app.util.LifecycleValue
import co.softov.morestuff.android.domain.enums.Priority.*
import co.softov.morestuff.android.presentation.Screens
import co.softov.morestuff.android.presentation.content.adapter.ChatAdapter
import co.softov.morestuff.android.presentation.content.adapter.view_holder.ChatViewHolderFactory
import co.softov.morestuff.android.presentation.dashboard.options.DatePickerFragment
import co.softov.morestuff.android.presentation.list.ListsFragment
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
    private lateinit var layoutManager: StaggeredGridLayoutManager
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

    override fun onResume() {
        super.onResume()
        binding.editChatInput.showSoftInputOnFocus = true
    }

    override fun onPause() {
        super.onPause()
        binding.editChatInput.clearFocus()
        binding.editChatInput.showSoftInputOnFocus = false
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

        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        binding.apply {
            buttonShowTaskList.setOnDebouncedClickListener {
                viewModel.showTaskList()
            }

            buttonActionToday.setOnDebouncedClickListener {
                viewModel.selectedTodayPriority()
            }

            buttonActionTomorrow.setOnDebouncedClickListener {
                viewModel.selectedTomorrowPriority()
            }

            buttonActionLater.setOnDebouncedClickListener {
                viewModel.selectedLaterPriority()
            }
        }
    }

    private fun setupChatInput() {
        binding.apply {
            editChatInput.showSoftInputOnFocus = false
            editChatInput.addTextChangedListener(ChatInputTextWatcher())

            buttonChatInputSend.setOnDebouncedClickListener {
                editChatInput.text?.let {
                    viewModel.addNewTask(it.toString())
                    editChatInput.text = null
                }
            }
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

        binding.apply {
            when (state.priority) {
                is Later -> {
                    buttonActionToday.isSelected = false
                    buttonActionTomorrow.isSelected = false
                    buttonActionLater.isSelected = true
                }
                is Today -> {
                    buttonActionToday.isSelected = true
                    buttonActionTomorrow.isSelected = false
                    buttonActionLater.isSelected = false
                }
                is Tomorrow -> {
                    buttonActionToday.isSelected = false
                    buttonActionTomorrow.isSelected = true
                    buttonActionLater.isSelected = false
                }
            }
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

    inner class ChatInputTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Ignore
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Ignore
        }

        override fun afterTextChanged(text: Editable?) {
            binding.apply {
                when (text.isNullOrBlank()) {
                    true ->
                        if (buttonChatInputSend.isVisible) {
                            buttonShowTaskList.isVisible = true
                            buttonChatInputSend.isVisible = false
                        }

                    false -> {
                        if (buttonShowTaskList.isVisible) {
                            buttonShowTaskList.isVisible = false
                            buttonChatInputSend.isVisible = true
                        }
                    }
                }
            }
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
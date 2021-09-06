package co.softov.morestuff.android.presentation.content

import android.os.Bundle
import android.view.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.iterator
import androidx.fragment.app.ListFragment
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.presentation.fragment.BaseFragment
import co.softov.morestuff.android.app.util.LifecycleValue
import co.softov.morestuff.android.databinding.FragmentContentBinding
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.Priority.*
import co.softov.morestuff.android.presentation.Screens
import co.softov.morestuff.android.presentation.compose.viewMigration
import co.softov.morestuff.android.presentation.content.adapter.ChatAdapter
import co.softov.morestuff.android.presentation.content.adapter.view_holder.AppChatItem
import co.softov.morestuff.android.presentation.content.adapter.view_holder.UserChatItem
import co.softov.morestuff.android.presentation.dashboard.options.DatePickerFragment
import co.softov.morestuff.android.presentation.schedule_list.ListsFragment
import com.github.terrakok.cicerone.Router
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class ContentFragment : BaseFragment() {

    private val viewModel: ContentViewModel by viewModel {
        parametersOf(conductor)
    }

    val actions by lazy {
        ChatActions(
            confirmationAction = viewModel::userSelectedPriorityOption,
            scheduleAction = viewModel::scheduleResponse
        )
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(inflater.context).apply {
            viewMigration {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {

                    MessagesList(
                        actions = actions,
                        modifier = Modifier.weight(1f)
                    )

                    UserInput(
                        modifier = Modifier.weight(1f)
                    )

                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding = FragmentContentBinding.bind(view)
        setHasOptionsMenu(true)
//        initViews()
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    private fun initViews() {
        setupChatList()
        setupPriorities()
    }

    private fun setupPriorities() {
        binding.composeUserInput.viewMigration {
            UserInput()
        }
    }

    @Composable
    private fun UserInput(
        modifier: Modifier = Modifier
    ) {
        val state by viewModel.priorityState.collectAsState()

        Column(
            modifier = modifier
        ) {
            UserPriorityInput(
                currentPriority = state
            ) {
                when (it) {
                    is Later -> viewModel.selectedLaterPriority()
                    is Today -> viewModel.selectedTodayPriority()
                    is Tomorrow -> viewModel.selectedTomorrowPriority()
                }
            }

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

        binding.composeChat.viewMigration {
            MessagesList(actions)
        }

        /*val factory = ChatViewHolderFactory(actions)

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
        }*/
    }

    @Composable
    private fun MessagesList(
        actions: ChatActions,
        modifier: Modifier = Modifier
    ) {
        val pager = remember { viewModel.messagePager }

        val lazyPagingItems = pager.collectAsLazyPagingItems()

        LazyColumn(
            modifier = modifier,
            reverseLayout = true,
            verticalArrangement = Arrangement.Top
        ) {
            items(
                lazyPagingItems,
                key = { item -> item.id }
            ) { item ->
                when (item?.contentType) {
                    ContentType.USER_NEW_TASK -> UserChatItem(message = item)
                    ContentType.CONFIRM_NEW_TASK,
                    ContentType.TASK_REMINDER -> AppChatItem(message = item, actions = actions)
                }
            }
        }
    }

    /*inner class AdapterDataObserver : RecyclerView.AdapterDataObserver() {

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            if (positionStart == 0) {
                scrollToCurrentMessage()
            }
        }

        private fun scrollToCurrentMessage() {
            binding.recyclerChatMessages.scrollToPosition(0)
        }
    }*/

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
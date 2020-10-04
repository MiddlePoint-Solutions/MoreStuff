package co.softov.morestuff.androidApp.presentation.dashboard

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import co.softov.morestuff.android.R
import co.softov.morestuff.android.databinding.FragmentDashboardBinding
import co.softov.morestuff.androidApp.app.presentation.extension.observe
import co.softov.morestuff.androidApp.app.presentation.extension.setOnDebouncedClickListener
import co.softov.morestuff.androidApp.app.presentation.fragment.BaseContainerFragment
import co.softov.morestuff.androidApp.app.ui.GridRecyclerView
import co.softov.morestuff.androidApp.app.util.LifecycleValue
import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.presentation.dashboard.options.DatePickerFragment
import co.softov.morestuff.androidApp.presentation.dashboard.options.TimePickerFragment
import co.softov.morestuff.androidApp.presentation.dashboard.options.TimeOptionsAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.Calendar

class DashboardFragment : BaseContainerFragment() {

    private val viewModel: DashboardViewModel by viewModel {
        parametersOf(Conductor())
    }

    override val layoutResourceId: Int = R.layout.fragment_dashboard
    private var binding: FragmentDashboardBinding by LifecycleValue()
    private lateinit var optionsAdapter: TimeOptionsAdapter

    private val recyclerView: GridRecyclerView
        get() = binding.recyclerviewDashboardOptions

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDashboardBinding.bind(view)
        optionsAdapter = TimeOptionsAdapter { viewModel.userSelectedOption(it) }

        initViews()
        observe(viewModel.stateLiveData, ::render)
        viewModel.loadData()
    }

    private fun initViews() {
        binding.apply {
            dashboardDoneButton.isEnabled = false
            setupRecyclerView()

            dashboardTaskInput.addTextChangedListener {
                dashboardDoneButton.isEnabled = (it?.toString()?.isNotEmpty() ?: false)
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

            dashboardDoneButton.setOnDebouncedClickListener {
                val title = dashboardTaskInput.text?.toString() ?: "Empty Task"
                viewModel.createTask(title)
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            this.adapter = optionsAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            val resId: Int = R.anim.grid_layout_animation_enter_from_bottom
            layoutAnimation = AnimationUtils.loadLayoutAnimation(requireContext(), resId)
        }
    }

    private fun render(state: DashboardViewState) {
        setPriority(state.priority)
        optionsAdapter.setCurrentOption(state.currentItemId)

        val shouldAnimate = state.items.size != optionsAdapter.itemCount
        optionsAdapter.submitList(state.items) {
            if (shouldAnimate) recyclerView.scheduleLayoutAnimation()
        }
    }

    private fun setPriority(priority: Priority) {
        binding.apply {
            when (priority) {
                is Priority.Later -> {
                    buttonActionToday.isSelected = false
                    buttonActionTomorrow.isSelected = false
                    buttonActionLater.isSelected = true
                }
                is Priority.Today -> {
                    buttonActionToday.isSelected = true
                    buttonActionTomorrow.isSelected = false
                    buttonActionLater.isSelected = false
                }
                is Priority.Tomorrow -> {
                    buttonActionToday.isSelected = false
                    buttonActionTomorrow.isSelected = true
                    buttonActionLater.isSelected = false
                }
            }
        }
    }

    inner class Conductor : DashboardConductor {

        override fun showTaskList() {
            parentFragmentManager.popBackStack()
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
            }.show(childFragmentManager, "DatePicker")
        }

        private fun showTimePicker(withTime: Long = 0) {
            TimePickerFragment.createInstance(withTime) { time ->
                viewModel.userSetCustomTime(time)
            }.show(childFragmentManager, "TimePicker")
        }
    }
}

package co.softov.morestuff.android.presentation.list

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.NestedScrollingChild
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import co.softov.morestuff.android.R
import co.softov.morestuff.android.databinding.FragmentTasksPagerBinding
import co.softov.morestuff.android.app.presentation.fragment.BaseBottomSheetDialogFragment
import co.softov.morestuff.android.app.util.LifecycleValue
import co.softov.morestuff.android.presentation.list.ListsFragment.Pages.*
import co.softov.morestuff.android.presentation.list.schedule.all.AllScheduleFragment
import co.softov.morestuff.android.presentation.list.schedule.later.LaterScheduleFragment
import co.softov.morestuff.android.presentation.list.schedule.today.TodayScheduleFragment
import co.softov.morestuff.android.presentation.list.schedule.tomorrow.TomorrowScheduleFragment
import co.softov.morestuff.android.presentation.list.tasks.active.ActiveTasksFragment
import co.softov.morestuff.android.presentation.list.tasks.complete.CompleteTasksFragment
import com.google.android.material.bottomsheet.BottomSheetDialog

class ListsFragment : BaseBottomSheetDialogFragment() {

    override val layoutResourceId: Int = R.layout.fragment_tasks_pager
    private var binding: FragmentTasksPagerBinding by LifecycleValue()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        val height = getWindowHeight()
        val view = LayoutInflater.from(context).inflate(layoutResourceId, null)
        dialog.setContentView(
            view,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        )

        val bottomSheetDialog = dialog as BottomSheetDialog
        bottomSheetDialog.behavior.isFitToContents = false
        bottomSheetDialog.behavior.peekHeight = height / 2
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTasksPagerBinding.bind(view)
        binding.viewPager.adapter = TasksPagerAdapter(childFragmentManager)
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    private inner class TasksPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(
        fm,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

        private val pages = arrayOf(
            PAGE_ACTIVE_SCHEDULES,
            PAGE_ACTIVE_TODAY,
            PAGE_ACTIVE_TOMORROW,
            PAGE_ACTIVE_LATER,
            PAGE_COMPLETE_TASKS,
            PAGE_ACTIVE_TASKS
        )

        override fun getCount(): Int = pages.size

        override fun getItem(position: Int): Fragment =
            when (pages[position]) {
                PAGE_ACTIVE_TASKS -> ActiveTasksFragment()
                PAGE_COMPLETE_TASKS -> CompleteTasksFragment()
                PAGE_ACTIVE_SCHEDULES -> AllScheduleFragment()
                PAGE_ACTIVE_TODAY -> TodayScheduleFragment()
                PAGE_ACTIVE_TOMORROW -> TomorrowScheduleFragment()
                PAGE_ACTIVE_LATER -> LaterScheduleFragment()
            }

        override fun getPageTitle(position: Int): CharSequence? {
            return pages[position].title
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            super.setPrimaryItem(container, position, `object`)

            val currentFragment = `object` as BaseListFragment

            currentFragment.view?.let { view ->
                for (i in 0 until count) {
                    (container.getChildAt(i) as? NestedScrollingChild)?.isNestedScrollingEnabled =
                        false
                }

                val currentNestedScrollView: NestedScrollingChild = view as NestedScrollingChild
                currentNestedScrollView.isNestedScrollingEnabled = true
                container.requestLayout()
            }
        }
    }

    enum class Pages(val title: String) {
        PAGE_ACTIVE_TASKS("Active"),
        PAGE_COMPLETE_TASKS("Complete"),
        PAGE_ACTIVE_SCHEDULES("Schedule"),
        PAGE_ACTIVE_TODAY("Today"),
        PAGE_ACTIVE_TOMORROW("Tomorrow"),
        PAGE_ACTIVE_LATER("Later")
    }
}

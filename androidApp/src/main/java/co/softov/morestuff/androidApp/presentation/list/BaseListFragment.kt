package co.softov.morestuff.androidApp.presentation.list

import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import co.softov.morestuff.android.R
import co.softov.morestuff.android.databinding.FragmentTaskListBinding
import co.softov.morestuff.androidApp.app.ui.ContextMenuRecyclerView
import co.softov.morestuff.androidApp.app.util.LifecycleValue
import timber.log.Timber

abstract class BaseListFragment() : Fragment(R.layout.fragment_task_list) {

    abstract fun rescheduleTaskOneHour(taskId: Long)
    abstract fun rescheduleTaskTomorrow(taskId: Long)
    abstract fun rescheduleTaskLater(taskId: Long)
    abstract fun rescheduleTaskComplete(taskId: Long)

    protected var binding: FragmentTaskListBinding by LifecycleValue()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTaskListBinding.bind(view)
        registerForContextMenu(view)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.setHeaderTitle("Reschedule Task")
        menu.add(0, R.id.reschedule_tasks_1_hour, 0, "1 - Hour")
            .setOnMenuItemClickListener(itemClickListener)
        menu.add(0, R.id.reschedule_tasks_tomorrow, 0, "Tomorrow")
            .setOnMenuItemClickListener(itemClickListener)
        menu.add(0, R.id.reschedule_tasks_later, 0, "Later")
            .setOnMenuItemClickListener(itemClickListener)
        menu.add(0, R.id.reschedule_tasks_complete, 0, "Complete")
            .setOnMenuItemClickListener(itemClickListener)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        Timber.d("onContextItemSelected: ${item.groupId}")
        val info = item.menuInfo as ContextMenuRecyclerView.RecyclerViewContextMenuInfo
        when (item.itemId) {
            R.id.reschedule_tasks_1_hour -> {
                rescheduleTaskOneHour(info.id)
            }
            R.id.reschedule_tasks_tomorrow -> {
                rescheduleTaskTomorrow(info.id)
            }
            R.id.reschedule_tasks_later -> {
                rescheduleTaskLater(info.id)
            }
            R.id.reschedule_tasks_complete -> {
                rescheduleTaskComplete(info.id)
            }
        }
        return true
    }

    private val itemClickListener = MenuItem.OnMenuItemClickListener {
        onContextItemSelected(it)
    }
}
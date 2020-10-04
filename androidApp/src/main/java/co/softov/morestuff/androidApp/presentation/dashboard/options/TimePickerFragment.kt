package co.softov.morestuff.androidApp.presentation.dashboard.options

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class TimePickerFragment
private constructor(
    private val timeSetAction: (time: Long) -> Unit
) : DialogFragment(),
    TimePickerDialog.OnTimeSetListener {

    private val c = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        arguments?.let {
            val initialTime = it.getLong(EXTRA_INITIAL_TIME)
            if (initialTime > 0) {
                c.timeInMillis = initialTime
            }
        }

        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(
            activity,
            this,
            hour,
            minute,
            DateFormat.is24HourFormat(activity)
        )
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val time = c.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }.timeInMillis
        timeSetAction(time)
    }

    companion object {

        const val EXTRA_INITIAL_TIME: String = "EXTRA_INITIAL_TIME"

        fun createInstance(
            initialTime: Long = 0,
            timeSetAction: (time: Long) -> Unit
        ): TimePickerFragment {
            val instance = TimePickerFragment(timeSetAction)
            val bundle = Bundle()
            bundle.putLong(EXTRA_INITIAL_TIME, initialTime)
            instance.arguments = bundle
            return instance
        }
    }
}
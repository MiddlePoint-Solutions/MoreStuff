package co.softov.morestuff.android.presentation.priority

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class PriorityFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return PriorityView(requireContext()).apply {
            addCircle(100f, 100f, 50f, Color.GREEN)
            addCircle(400f, 300f, 100f, Color.BLUE)
        }
    }
}
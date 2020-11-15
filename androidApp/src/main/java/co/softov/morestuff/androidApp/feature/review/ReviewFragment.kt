package co.softov.morestuff.androidApp.feature.review

import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import co.softov.morestuff.android.R
import co.softov.morestuff.android.databinding.FragmentReviewBinding
import co.softov.morestuff.androidApp.app.presentation.fragment.BaseFragment
import co.softov.morestuff.androidApp.app.util.LifecycleValue
import co.softov.morestuff.androidApp.domain.model.ScheduleWithTitle
import com.yuyakaido.android.cardstackview.*
import timber.log.Timber

class ReviewFragment : BaseFragment(), CardStackListener {

    override val layoutResourceId: Int
        get() = R.layout.fragment_review

    private var binding: FragmentReviewBinding by LifecycleValue()

    private val adapter by lazy { ReviewStackAdapter(createTestSchedules()) }
    private val manager by lazy { CardStackLayoutManager(requireContext(), this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentReviewBinding.bind(view)
        initialize()
    }

    private fun initialize() {
        manager.setStackFrom(StackFrom.Top)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        binding.apply {
            cardStackView.layoutManager = manager
            cardStackView.adapter = adapter
            cardStackView.itemAnimator.apply {
                if (this is DefaultItemAnimator) {
                    supportsChangeAnimations = false
                }
            }
        }
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Timber.d("onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction) {
        Timber.d("onCardSwiped: p = ${manager.topPosition}, d = $direction")
        /*if (manager.topPosition == adapter.itemCount - 5) {
            paginate()
        }*/
    }

    override fun onCardRewound() {
        Timber.d("onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Timber.d("onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
        Timber.d("onCardAppeared: ($position) TODO: which item")
    }

    override fun onCardDisappeared(view: View, position: Int) {
        Timber.d("onCardDisappeared: ($position) TODO: which item")
    }


    private fun createTestSchedules() = mutableListOf<ScheduleWithTitle>().apply {
        add(ScheduleWithTitle(size.toLong(),size.toLong(),null, "Create review demo"))
        add(ScheduleWithTitle(size.toLong(),size.toLong(),null, "Test review"))
        add(ScheduleWithTitle(size.toLong(),size.toLong(),null, "Think about behaviour"))
        add(ScheduleWithTitle(size.toLong(),size.toLong(),null, "Remember this is supposed to be playful"))
        add(ScheduleWithTitle(size.toLong(),size.toLong(),null, "Create design"))
    }
}
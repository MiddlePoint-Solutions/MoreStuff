package co.softov.morestuff.android.feature.review

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.softov.morestuff.android.R
import co.softov.morestuff.android.databinding.LayoutReviewItemBinding
import co.softov.morestuff.android.app.presentation.extension.inflateView
import co.softov.morestuff.android.domain.model.ScheduleWithTitle

class ReviewStackAdapter(
    private var items: List<ScheduleWithTitle> = emptyList()
) : RecyclerView.Adapter<ReviewStackAdapter.ReviewItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewItemViewHolder {
        return ReviewItemViewHolder(parent.inflateView(R.layout.layout_review_item))
    }

    override fun onBindViewHolder(holder: ReviewItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ReviewItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: ScheduleWithTitle) {
            LayoutReviewItemBinding.bind(itemView).apply {
                reviewItemTitle.text = item.taskTitle
            }
        }

    }

}
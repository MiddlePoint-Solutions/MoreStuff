package co.softov.morestuff.android.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class ComposeListAdapter<T, VH : ComposeViewHolder<T>>(
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, VH>(diffCallback) {

    override fun onViewRecycled(holder: VH) {
        holder.composeView.disposeComposition()
        super.onViewRecycled(holder)
    }
}

abstract class ComposeRecyclerViewAdapter<VH : ComposeViewHolder<*>> : RecyclerView.Adapter<VH>() {

    override fun onViewRecycled(holder: VH) {
        holder.composeView.disposeComposition()
        super.onViewRecycled(holder)
    }
}

abstract class ComposeViewHolder<T>(
    val composeView: ComposeView
) : RecyclerView.ViewHolder(composeView) {

    @Composable
    abstract fun ViewHolder(input: T)

    init {
        composeView.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )
    }

    fun bindViewHolder(input: T) {
        composeView.setContent {
            ViewHolder(input)
        }
    }
}
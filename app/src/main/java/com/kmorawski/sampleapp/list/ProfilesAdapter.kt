package com.kmorawski.sampleapp.list

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kmorawski.sampleapp.Logger
import com.kmorawski.sampleapp.R
import com.kmorawski.sampleapp.databinding.ProfileItemBinding

class ProfilesAdapter(private val logger: Logger)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(), OnProfileSelectedListener  {
    private val PROFILE_TYPE = 0
    private val LOADING_TYPE = 1
    private val ERROR_MESSAGE_TYPE = 2

    /**
     * Event raised when scrolling reaches the end of the list
     * (as detected by the presence of the last element, representing a spinner).
     * Should be set up to trigger retrieving more profiles.
     */
    var onRetrieveMore: () -> Unit = {
        // do nothing by default
    }
    var onProfileSelected: (View, ProfileViewModel) -> Unit = {
        _, _ -> // nothing by default
    }
    var onErrorStateChanged: (Boolean) -> Unit = {
        // do nothing by default
    }

    /* initiating the list with a single "load more" element
     * ensures that first profiles will get loaded */
    private val items = mutableListOf<ProfileItem>(LoadMoreIndicatorViewModel)

    fun setItems(newItems: List<ProfileItem>) {
        onErrorStateChanged(newItems.any { it is ErrorViewModel })

        val oldList = items.toList()
        items.apply {
            clear()
            addAll(newItems)
        }
        dispatchUpdates(oldList, items)

        // for simplicity we assume the list won't be empty (which is not safe)
        logger.debug("New items were put on the list: ${newItems.size} in total, the last one is ${newItems.last()}.")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            with(LayoutInflater.from(parent.context)) {
                when (viewType) {
                    PROFILE_TYPE -> {
                        ProfileItemBinding
                                .inflate(this, parent, false)
                                .let(::ProfileHolder)
                    }
                    LOADING_TYPE -> {
                        inflate(R.layout.loading_item, parent, false)
                                .let(::LoadingHolder)
                    }
                    ERROR_MESSAGE_TYPE ->
                        inflate(R.layout.error_item, parent, false)
                                .let(::ErrorHolder)
                    else ->
                            TODO("Unsupported viewType value: $viewType. Review the implementation.")
                }
            }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when {
            holder is ProfileHolder && item is ProfileViewModel -> {
                holder.bind(item, this)
            }
            holder is ErrorHolder && item is ErrorViewModel -> {
                onErrorStateChanged(true)
                holder.setError(item.error)
            }
            holder is LoadingHolder -> {
                logger.debug("Binding LOADING indicator at position $position")
                onRetrieveMore()
            }
        }
    }

    override fun onSelected(view: View, profileViewModel: ProfileViewModel) =
            onProfileSelected(view, profileViewModel)

    fun isFullWidthAt(position: Int) = items[position] !is ProfileViewModel

    override fun getItemViewType(position: Int) = when (items[position]) {
        is ProfileViewModel -> PROFILE_TYPE
        is LoadMoreIndicatorViewModel -> LOADING_TYPE
        is ErrorViewModel -> ERROR_MESSAGE_TYPE
    }

    private fun dispatchUpdates(oldItems: List<ProfileItem>, newItems: List<ProfileItem>) =
            ProfilesDiffUtilCallback(
                    oldItems = oldItems,
                    newItems = newItems)
                    .let {
                        DiffUtil.calculateDiff(it)
                    }
                    .dispatchUpdatesTo(this)
}
package com.kmorawski.sampleapp.list

import android.support.v7.util.DiffUtil

internal class ProfilesDiffUtilCallback(
        private val oldItems: List<ProfileItem>,
        private val newItems: List<ProfileItem>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            areSame(oldItemPosition, newItemPosition)

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            areSame(oldItemPosition, newItemPosition)

    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    private fun areSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        if (oldItem === LoadMoreIndicatorViewModel && newItem === LoadMoreIndicatorViewModel) {
            /* we don't want the RecyclerView to ever reuse these layouts,
             * because it would kill the "infinite scrolling" functionality.
             * we want onBindViewHolder to be invoked for them every time. */
            return false
        }
        return oldItem == newItem
    }
}
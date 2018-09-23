package com.kmorawski.sampleapp.list

sealed class RetrievalResult(protected val profiles: List<ProfileItem>) {
    abstract val items: List<ProfileItem>

    class Success(profiles: List<ProfileItem>, private val loadMore: Boolean) : RetrievalResult(profiles) {
        override val items: List<ProfileItem>
            get() = when {
                loadMore -> profiles + LoadMoreIndicatorViewModel
                else -> profiles
            }

        override fun toString() = "Success(items=$items, loadMore=$loadMore)"
    }

    class Error(profiles: List<ProfileItem>, val error: Throwable) : RetrievalResult(profiles) {
        override val items: List<ProfileItem>
            get() = profiles + ErrorViewModel(error)

        override fun toString() = "Error(items=$items, error=$error)"
    }
}
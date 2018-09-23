package com.kmorawski.sampleapp.list

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.kmorawski.sampleapp.R
import com.kmorawski.sampleapp.requireAtLeast
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Detects - signalling them to subscribers - overscroll occurrences happening in the bottom direction.
 * Used for the "scroll further to retry after an error" functionality.
 */
internal class OverscrollAwareGridLayoutManager(private val context: Context?, columnSpan: Int)
    : GridLayoutManager(context, columnSpan) {
    private val minimumOverscroll: Int by lazy {
        context
                ?.resources
                ?.getInteger(R.integer.minimum_overscroll_size)
                ?.requireAtLeast(0) { "minimum_overscroll_size" }
                ?: 0
    }

    /**
     * Fires notification signalling each detected overscroll
     * (in the bottom direction, and at least of the [minimumOverscroll] size)
     */
    val bottomOverscrollsOccurrences: Observable<Unit>
        get() = overscrollOccurrences
                .filter {
                    overscrollDetectionEnabled && it >= minimumOverscroll
                }
                .map { Unit }
                .hide()

    /**
     * Enables or disables overscroll detection functionality (duh)
     */
    var overscrollDetectionEnabled = false

    private val overscrollOccurrences = PublishSubject.create<Int>()

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?) =
            super.scrollVerticallyBy(dy, recycler, state).also {
                overscrollOccurrences.onNext(dy - it)
            }
}
package com.kmorawski.sampleapp.list

import android.support.v7.widget.GridLayoutManager

internal class SpanSizeLookup(
        private val profilesAdapter: ProfilesAdapter,
        private val columnSpan: Int) : GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int) = when {
        profilesAdapter.isFullWidthAt(position) -> columnSpan
        else -> 1
    }
}
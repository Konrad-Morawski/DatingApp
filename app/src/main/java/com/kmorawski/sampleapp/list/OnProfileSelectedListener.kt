package com.kmorawski.sampleapp.list

import android.view.View

interface OnProfileSelectedListener {
    fun onSelected(view: View, profileViewModel: ProfileViewModel)
}
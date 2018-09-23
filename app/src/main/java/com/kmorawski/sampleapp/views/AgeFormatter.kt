package com.kmorawski.sampleapp.views

interface AgeFormatter {
    fun toReadableString(age: Int?): String?
}
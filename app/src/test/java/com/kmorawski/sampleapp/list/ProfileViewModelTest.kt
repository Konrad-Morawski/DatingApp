package com.kmorawski.sampleapp.list

import com.kmorawski.sampleapp.api.Profile
import com.kmorawski.sampleapp.views.AgeFormatter
import junit.framework.Assert.assertEquals
import org.junit.Test

class ProfileViewModelTest {
    @Test
    fun `given profile with name and age - once converted to view model - name and age are presented correctly in separate lines`() {
        with(mockedProfile(name = "Amy", age = 29).convertedToViewModel()) {
            assertEquals("Amy", name)
            assertEquals("29 years", age)
        }
    }

    @Test
    fun `given profile with only name and no age - once converted to view model - name is presented correctly in a single line`() {
        val viewModel = mockedProfile(name = "Amy", age = null).convertedToViewModel()
        assertEquals("Amy", viewModel.name)
    }

    @Test
    fun `given profile with only age and no name - once converted to view model - age is presented correctly in a single line`() {
        val viewModel = mockedProfile(name = null, age = 29).convertedToViewModel()
        assertEquals("29 years", viewModel.age)
    }
}

fun mockedProfile(name: String?, age: Int?) = Profile(
        name = name,
        age = age,
        // fields irrelevant for the tests
        surname = null,
        photo = null,
        region = null)

fun Profile.convertedToViewModel() = toViewModel(
        object : AgeFormatter {
            override fun toReadableString(age: Int?): String? = age?.let { "$it years" }
        })
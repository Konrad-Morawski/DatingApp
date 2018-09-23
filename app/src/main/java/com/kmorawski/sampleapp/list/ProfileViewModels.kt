package com.kmorawski.sampleapp.list

import com.kmorawski.sampleapp.api.Profile
import com.kmorawski.sampleapp.views.AgeFormatter
import java.io.Serializable

sealed class ProfileItem : Serializable

/**
 * Note: it's Serializable so that we can pass the object from list Activity to the details Activity.
 * It could be done differently, eg. by using a repository object (cache), and only passing
 * the profile ID.
 */
data class ProfileViewModel(
        val name: String,
        val age: String,
        val imageUrl: String?,
        val region: String) : ProfileItem(), Serializable

/**
 * Note: normally this wouldn't be a singleton, and it would possibly store the page number
 * of the results we now want to retrieve.
 * As the API returns randomized data, paging is completely irrelevant.
 * All we care is that we want more.
 */
object LoadMoreIndicatorViewModel : ProfileItem()

class ErrorViewModel(val error: Throwable) : ProfileItem()

/**
 * Converts the API response model ([Profile]) to a [ProfileViewModel] instance.
 */
fun Profile.toViewModel(ageFormatter: AgeFormatter): ProfileViewModel {
    val age = ageFormatter.toReadableString(age).orEmpty()
    return ProfileViewModel(
            name = name.orEmpty(),
            age = age,
            imageUrl = photo,
            region = region.orEmpty())
}
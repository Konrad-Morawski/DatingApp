package com.kmorawski.sampleapp.api

import io.reactivex.Single

interface ProfilesRetriever {
    /**
     * Retrieves the given number ([amount]) of profiles
     */
    fun retrieve(amount: Int): Single<List<Profile>>
}
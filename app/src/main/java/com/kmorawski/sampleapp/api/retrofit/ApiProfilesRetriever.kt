package com.kmorawski.sampleapp.api.retrofit

import com.kmorawski.sampleapp.api.Profile
import com.kmorawski.sampleapp.api.ProfilesRetriever
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class ApiProfilesRetriever(private val apiService: ApiService) : ProfilesRetriever {
    override fun retrieve(amount: Int): Single<List<Profile>> = apiService
            .getProfiles(amount)
            .subscribeOn(Schedulers.io())
            .singleOrError()
}
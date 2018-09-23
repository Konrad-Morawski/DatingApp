package com.kmorawski.sampleapp.api.retrofit

import com.kmorawski.sampleapp.api.Profile
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    /**
     * Hardcoded 'ext' parameter ensures the response contains additional, random data
     * (such as photo).
     */
    @GET("/api/?ext")
    fun getProfiles(@Query("amount") amount: Int): Observable<List<Profile>>
}
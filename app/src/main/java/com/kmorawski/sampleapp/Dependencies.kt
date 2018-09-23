package com.kmorawski.sampleapp

import android.content.res.Resources
import android.util.Log
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.kmorawski.sampleapp.api.ProfilesRetriever
import com.kmorawski.sampleapp.api.retrofit.ApiProfilesRetriever
import com.kmorawski.sampleapp.api.retrofit.ApiService
import com.kmorawski.sampleapp.list.ListConfig
import com.kmorawski.sampleapp.views.AgeFormatter
import com.kmorawski.sampleapp.views.ProfilesListViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun getApiModule(resources: Resources) = module {
    single {
        Retrofit.Builder()
                .baseUrl(BuildConfig.API_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ApiService::class.java)
    }

    single<ProfilesRetriever> {
        ApiProfilesRetriever(get())
    }

    single<AgeFormatter> {
        object : AgeFormatter {
            override fun toReadableString(age: Int?) = age?.let {
                resources.getString(R.string.years, it)
            }
        }
    }

    single {
        ListConfig(
                resources.getInteger(R.integer.page_size).requireAtLeast(2) { "page_size" },
                resources.getInteger(R.integer.max_items).requireAtLeast(2) { "max_items" })
    }
}

val viewModelsModule = module {
    viewModel {
        ProfilesListViewModel(get(), get(), get(), get())
    }
}

val loggerModule = module {
    single<Logger> {
        object : Logger {
            override fun debug(message: String) {
                Log.d("tag", message)
            }
        }
    }
}
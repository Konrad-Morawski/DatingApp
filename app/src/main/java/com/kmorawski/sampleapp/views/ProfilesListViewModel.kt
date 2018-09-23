package com.kmorawski.sampleapp.views

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.kmorawski.sampleapp.Logger
import com.kmorawski.sampleapp.api.Profile
import com.kmorawski.sampleapp.api.ProfilesRetriever
import com.kmorawski.sampleapp.list.ListConfig
import com.kmorawski.sampleapp.list.ProfileItem
import com.kmorawski.sampleapp.list.ProfileViewModel
import com.kmorawski.sampleapp.list.RetrievalResult
import com.kmorawski.sampleapp.list.toViewModel
import io.reactivex.disposables.Disposable

class ProfilesListViewModel(
        private val profilesRetriever: ProfilesRetriever,
        private val listConfig: ListConfig,
        private val ageFormatter: AgeFormatter,
        private val logger: Logger) : ViewModel() {
    private val retrievedProfiles = mutableListOf<Profile>()
    private val profilesLiveData = MutableLiveData<RetrievalResult>()
    private var apiSubscription: Disposable? = null

    private val hasLoadedAllProfiles: Boolean
        /* normally we'd also check whether the last retrieved portion wasn't empty,
         * or containing less items than the page size, which would indicate we got to the bottom.
         * it's not going to happen here though, because the API serves randomized data
         * and is infinite. */
        get() = retrievedProfiles.size >= listConfig.maxItems

    val profiles: LiveData<RetrievalResult>
        get() = profilesLiveData

    fun retrieveMoreProfiles() {
        if (hasLoadedAllProfiles) {
            return
        }
        apiSubscription?.dispose()
        apiSubscription = profilesRetriever
                .retrieve(listConfig.pageSize)
                .doOnSuccess {
                    retrievedProfiles += it
                }
                .subscribe(
                        {
                            logger.debug("Successfully loaded more profiles (" +
                                    when {
                                        hasLoadedAllProfiles -> "more to come"
                                        else -> "and no more left"
                                    } +
                                    ")")
                            postValue {
                                RetrievalResult.Success(this, hasLoadedAllProfiles.not())
                            }
                        },
                        { error ->
                            logger.debug("Error occurred while loading profiles: $error")
                            postValue {
                                RetrievalResult.Error(this, error)
                            }
                        })
    }

    fun retry() = postValue {
        /* under current implementation, simply posting a "loading" view again will cause the View
         * to retrigger [retrieveMoreProfiles] */
        RetrievalResult.Success(this, hasLoadedAllProfiles.not())
    }

    private fun postValue(createResult: List<ProfileItem>.() -> RetrievalResult) =
            createResult(toViewModels(retrievedProfiles))
                    .let(profilesLiveData::postValue)

    override fun onCleared() {
        apiSubscription?.dispose()
        retrievedProfiles.clear()
        super.onCleared()
    }

    private fun toViewModels(profiles: List<Profile>): List<ProfileViewModel> = profiles.map {
        it.toViewModel(ageFormatter)
    }
}
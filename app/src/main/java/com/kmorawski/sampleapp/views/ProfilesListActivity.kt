package com.kmorawski.sampleapp.views

import android.app.ActivityOptions
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.kmorawski.sampleapp.Logger
import com.kmorawski.sampleapp.R
import com.kmorawski.sampleapp.list.OverscrollAwareGridLayoutManager
import com.kmorawski.sampleapp.list.ProfileViewModel
import com.kmorawski.sampleapp.list.ProfilesAdapter
import com.kmorawski.sampleapp.list.SpanSizeLookup
import com.kmorawski.sampleapp.requireAtLeast
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_profiles_list.profiles
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit
import android.util.Pair as AndroidPair

const val PROFILE_KEY = "Profile"

class ProfilesListActivity : AppCompatActivity() {
    private val listModel by viewModel<ProfilesListViewModel>()
    private lateinit var profilesAdapter: ProfilesAdapter
    private val listLayoutManager: OverscrollAwareGridLayoutManager by lazy {
        OverscrollAwareGridLayoutManager(this, columnSpan)
                .apply {
                    spanSizeLookup = SpanSizeLookup(profilesAdapter, columnSpan)
                }
    }
    private val columnSpan: Int by lazy {
        resources
                .getInteger(R.integer.column_span)
                .requireAtLeast(1) { "column_span" }
    }
    private val retryDelay: Long by lazy {
        resources
                .getInteger(R.integer.retry_delay_ms)
                .requireAtLeast(0) { "retry_delay_ms" }
                .toLong()
    }
    private val logger by inject<Logger>()

    private var retryAttemptsSubscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profiles_list)
        setUpProfilesList()
        setUpViewModel()
    }

    override fun onDestroy() {
        retryAttemptsSubscription?.dispose()
        super.onDestroy()
    }

    private fun setUpProfilesList() {
        profilesAdapter = ProfilesAdapter(logger)
        profilesAdapter.let {
            it.onRetrieveMore = listModel::retrieveMoreProfiles
            it.onProfileSelected = this::openDetailsActivity
            it.onErrorStateChanged = {
                // occurrence of network errors should enable the "scroll again to retry" functionality
                listLayoutManager.overscrollDetectionEnabled = it
            }
        }

        with(profiles) {
            layoutManager = listLayoutManager
            adapter = profilesAdapter
            itemAnimator = null
        }

        retryAttemptsSubscription = listLayoutManager
                .bottomOverscrollsOccurrences
                .doOnNext {
                    // immediately - we don't want the retry requests to be piling up
                    listLayoutManager.overscrollDetectionEnabled = false
                }
                .delay(retryDelay, TimeUnit.MILLISECONDS)
                .subscribe {
                    logger.debug("Attempting to reload the list based on user's intention")
                    listModel.retry()
                }
    }

    private fun setUpViewModel() = listModel.profiles.observe(
            this,
            Observer {
                profilesAdapter.setItems(requireNotNull(it).items)
            })

    private fun openDetailsActivity(view: View, profile: ProfileViewModel) =
            Intent(this, ProfileDetailsActivity::class.java)
                    .putExtra(PROFILE_KEY, profile)
                    .let {
                        startActivity(it, sceneTransitionOptions(view))
                    }

    private fun sceneTransitionOptions(view: View): Bundle =
            ActivityOptions
                    .makeSceneTransitionAnimation(this, *getSharedElements(view))
                    .toBundle()

    private fun getSharedElements(profileView: View) =
            mapOf(
                    R.id.profile_image to R.string.image_transition,
                    R.id.name to R.string.name_transition,
                    R.id.age to R.string.age_transition)
            .mapKeys { (id, _) ->
                profileView.findViewById<View>(id)
            }
            .mapValues { (_, id) ->
                getString(id)
            }
            .map { (view, transitionName) ->
                AndroidPair<View, String>(view, transitionName)
            }
            .toTypedArray()
}
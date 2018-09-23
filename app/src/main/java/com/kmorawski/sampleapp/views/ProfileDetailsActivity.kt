package com.kmorawski.sampleapp.views

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AlphaAnimation
import com.kmorawski.sampleapp.Logger
import com.kmorawski.sampleapp.R
import com.kmorawski.sampleapp.databinding.ActivityProfileDetailsBinding
import com.kmorawski.sampleapp.list.ProfileViewModel
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_profile_details.panel
import kotlinx.android.synthetic.main.profile_details.captions_background
import kotlinx.android.synthetic.main.profile_details.full_screen_image
import kotlinx.android.synthetic.main.profile_details.location
import org.koin.android.ext.android.inject

class ProfileDetailsActivity : AppCompatActivity() {
    private val locationTextFadeInDuration: Long by lazy {
        resources.getInteger(R.integer.location_fade_in_speed_ms).toLong()
    }
    private val backgroundColorFadeInDuration: Long by lazy {
        resources.getInteger(R.integer.highlight_captions_background_speed_ms).toLong()
    }
    private val logger by inject<Logger>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil
                .setContentView<ActivityProfileDetailsBinding>(this, R.layout.activity_profile_details)
                .loadProfile()
        setUpDragToDismiss()
    }

    override fun onPause() {
        super.onPause()
        // for smoother return to the previous screen
        overridePendingTransition(0, 0);
    }

    private fun ActivityProfileDetailsBinding.loadProfile() {
        val profile = intent?.retrieveProfile()
        when {
            profile != null -> {
                displayProfile(profile)
            }
            else -> {
                // this would indicate an implementation error
                finish()
            }
        }
    }

    private fun ActivityProfileDetailsBinding.displayProfile(profile: ProfileViewModel) {
        logger.debug("About to display a profile:\n$profile")
        model = profile
        executePendingBindings()

        val animation = AlphaAnimation(0.0F, 1.0F).apply {
            duration = locationTextFadeInDuration
        }
        location.startAnimation(animation)
        loadImageWithPalette(profile.imageUrl, full_screen_image) {
            window.statusBarColor = getDominantColor(getColorCompat(android.R.color.black))

            darkMutedSwatch?.rgb?.let {
                captions_background.setBackgroundColorGradually(
                        colorFrom = getColorCompat(android.R.color.transparent),
                        colorTo = it,
                        durationMs = backgroundColorFadeInDuration)
                background.setBackgroundColor(it)
            }
        }
    }

    @ColorInt
    private fun getColorCompat(@ColorRes colorId: Int) = ContextCompat.getColor(this, colorId)

    private fun Intent.retrieveProfile(): ProfileViewModel? = extras[PROFILE_KEY] as? ProfileViewModel

    private fun setUpDragToDismiss() = panel.addPanelSlideListener(OnSliddenDownExit())

    private inner class OnSliddenDownExit : SlidingUpPanelLayout.PanelSlideListener {
        override fun onPanelStateChanged(
                panel: View?,
                previousState: SlidingUpPanelLayout.PanelState?,
                newState: SlidingUpPanelLayout.PanelState?) {
            if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                logger.debug("Screen dismissed by gesture")
                finish()
            }
        }

        override fun onPanelSlide(panel: View?, slideOffset: Float) {
            // ignored
        }
    }
}
package com.kmorawski.sampleapp.views

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.support.annotation.ColorInt
import android.support.v7.graphics.Palette
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.florent37.glidepalette.GlidePalette

internal fun Activity.loadImageWithPalette(
        imageUrl: String?,
        imageView: ImageView,
        onPaletteRetrieved: Palette.() -> Unit) {
    imageUrl ?: return
    Glide
            .with(this)
            .load(imageUrl)
            .listener(applyPalette(imageUrl, onPaletteRetrieved))
            .into(imageView)
}

private fun applyPalette(imageUrl: String?, onPaletteRetrieved: Palette.() -> Unit) =
        GlidePalette
                .with(imageUrl)
                .intoCallBack {
                    it?.onPaletteRetrieved()
                }

internal fun View.setBackgroundColorGradually(@ColorInt colorFrom: Int, @ColorInt colorTo: Int, durationMs: Long) =
        ValueAnimator.ofObject(
                ArgbEvaluator(),
                colorFrom,
                colorTo)
            .apply {
                duration = durationMs
                interpolator = DecelerateInterpolator()
                addUpdateListener {
                    setBackgroundColor(it.animatedValue as Int)
                }
                start()
            }
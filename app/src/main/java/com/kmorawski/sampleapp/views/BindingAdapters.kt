package com.kmorawski.sampleapp.views

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kmorawski.sampleapp.R

@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String?) {
    when {
        url.isNullOrEmpty().not() -> Glide
                .with(imageView.context)
                .load(url)
                .apply(RequestOptions().fitCenter().override(imageView.height).placeholder(R.drawable.profile_placeholder))
                .into(imageView)
        else -> imageView.setImageBitmap(null)
    }
}
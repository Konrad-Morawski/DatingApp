package com.kmorawski.sampleapp.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.kmorawski.sampleapp.R
import com.kmorawski.sampleapp.databinding.ProfileItemBinding
import kotlinx.android.synthetic.main.error_item.view.error_icon
import kotlinx.android.synthetic.main.error_item.view.error_message
import java.net.UnknownHostException

internal class ProfileHolder(private val binding: ProfileItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(profile: ProfileViewModel, profileSelectedListener: OnProfileSelectedListener) = with(binding) {
        this.model = profile
        this.profileSelectedListener = profileSelectedListener
        executePendingBindings()
    }
}

internal class LoadingHolder(loadingView: View) : RecyclerView.ViewHolder(loadingView)

internal class ErrorHolder(errorView: View) : RecyclerView.ViewHolder(errorView) {
    fun setError(throwable: Throwable) {
        // yes it could also be done via databinding
        val isOffline = throwable is UnknownHostException
        itemView.error_message.text = when {
            isOffline -> itemView.resources.getString(R.string.you_are_offline)
            else -> itemView.resources.getString(R.string.network_error, throwable.message)
        }
        itemView.error_icon.setBackgroundResource(when {
            isOffline -> R.drawable.offline
            else -> R.drawable.error
        })
    }
}
package com.rudearts.githubsearch.extentions

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.rudearts.githubsearch.services.Loading
import com.rudearts.githubsearch.services.NetworkResult

@MainThread
inline fun <T> LiveData<NetworkResult<T>>.observeCall(
    owner: LifecycleOwner,
    crossinline onChanged: (NetworkResult<T>) -> Unit
): Observer<NetworkResult<T>> {
    val wrapperObserver = object : Observer<NetworkResult<T>> {
        override fun onChanged(t: NetworkResult<T>?) {
            if (t != null) {
                onChanged.invoke(t)
                if (t !is Loading) {
                    this@observeCall.removeObserver(this)
                }
            }
        }
    }
    observe(owner, wrapperObserver)
    return wrapperObserver
}
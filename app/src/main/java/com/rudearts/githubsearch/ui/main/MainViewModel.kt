package com.rudearts.githubsearch.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rudearts.githubsearch.model.GithubRepository
import com.rudearts.githubsearch.services.NetworkResult
import com.rudearts.githubsearch.services.NetworkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import java.util.concurrent.TimeUnit

class MainViewModel: ViewModel() {

    companion object {
        val SEARCH_COOLDOWN = TimeUnit.SECONDS.toMillis(1)
    }

    private val networkService by lazy { NetworkService() }

    fun loadMessages(query: String?, filterType: Int, filterText: String): LiveData<NetworkResult<List<GithubRepository>>> {
        return networkService.loadMessages(query, filterType, filterText)
            .flowOn(Dispatchers.IO)
            .asLiveData()
    }

}
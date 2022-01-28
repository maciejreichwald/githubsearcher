package com.rudearts.githubsearch.ui.main

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.rudearts.githubsearch.extentions.observeCall
import com.rudearts.githubsearch.model.GithubRepository
import com.rudearts.githubsearch.services.Loading
import com.rudearts.githubsearch.services.NetworkError
import com.rudearts.githubsearch.services.NetworkSuccess

import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import com.rudearts.githubsearch.R
import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.widget.*


class MainActivity : AppCompatActivity() {

    private val viewModel:MainViewModel by viewModels()

    private val adapter = MessageAdapter()

    private var searchView: SearchView? = null
    private lateinit var filterSpinner: Spinner
    private lateinit var filterInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setup()
    }

   private fun setup() {
        findViewById<Toolbar>(R.id.toolbar).also {
            it.setTitle(R.string.app_name)
            setSupportActionBar(it)
        }

        findViewById<SwipeRefreshLayout>(R.id.swipeRefresh).also { refreshLayout ->
            refreshLayout.setOnRefreshListener {
                loadMessages()
            }
        }

        findViewById<RecyclerView>(R.id.recyclerList).also { recycler ->
            recycler.adapter = adapter
        }

        adapter.setOnItemClickListener { url -> openRepoUrl(url) }

        setupFilters()

        loadMessages()
    }

    private fun setupFilters() {
        val filterAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.filter_items)).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        filterSpinner = findViewById<Spinner>(R.id.filterType).also {
            it.adapter = filterAdapter
            it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    loadMessages()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        val typingCounter = Handler(mainLooper)
        val typingCallback = Runnable { loadMessages() }

        filterInput = findViewById<EditText>(R.id.filterInput).also {
            it.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    typingCounter.removeCallbacks(typingCallback)
                    typingCounter.postDelayed(typingCallback, MainViewModel.SEARCH_COOLDOWN)
                    validateFilter()
                }

                override fun afterTextChanged(s: Editable?) { }
            })
        }
    }

    private fun validateFilter() {
        filterInput.error = when {
            filterInput.text.isEmpty() -> getString(R.string.filter_error)
            else -> null
        }
    }

    private fun openRepoUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private fun loadMessages() {
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val query = searchView?.query?.toString() ?: ""
        val filterType = filterSpinner.selectedItemPosition
        val filterText = filterInput.text.toString()

        if (filterText.isEmpty()) {
            validateFilter()
            return
        }

        viewModel.loadMessages(query, filterType, filterText).observeCall(this) { call ->
            when (call) {
                is Loading -> refreshLayout.isRefreshing = true
                is NetworkSuccess -> {
                    refreshLayout.isRefreshing = false
                    setupMessages(call.data)
                }
                is NetworkError -> {
                    refreshLayout.isRefreshing = false
                    Log.d("Error", call.exception.toString())
                }
            }
        }
    }

    private fun setupMessages(data: List<GithubRepository>) {
        adapter.update(data.sortedBy { it.name })

        findViewById<View>(R.id.emptyView).isVisible = data.isEmpty()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        searchView = menu.findItem(R.id.search_bar).actionView as SearchView?

        val typingCounter = Handler(mainLooper)
        val typingCallback = Runnable { loadMessages() }

        searchView?.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    typingCounter.post(typingCallback)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    typingCounter.removeCallbacks(typingCallback)
                    typingCounter.postDelayed(typingCallback, MainViewModel.SEARCH_COOLDOWN)
                    return false
                }
            })

        return super.onCreateOptionsMenu(menu)
    }


}
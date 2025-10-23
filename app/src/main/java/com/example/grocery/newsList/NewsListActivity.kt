package com.example.grocery.newsList

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.grocery.R
import com.example.grocery.states.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsListActivity : AppCompatActivity() {
    private val viewModel: NewsListViewModel by viewModels()

    companion object {
        const val ACTIVITY_TAG = "NewsListActivityss"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // call inside lifecycle-safe block
                viewModel.getTopHeadLInes("us")
                viewModel.newsState.collect { state ->
                    when (state) {
                        is UiState.Error -> Log.d(ACTIVITY_TAG, "Error: ${state.message}")
                        UiState.Loading -> Log.d(ACTIVITY_TAG, "Loading")
                        is UiState.Success -> Log.d(ACTIVITY_TAG, "Success: ${state.data}")
                    }
                }
            }
        }

    }
}
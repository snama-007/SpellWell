package com.wordwell.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.wordwell.libwwmw.WordWellServer
import com.wordwell.libwwmw.domain.models.WWResultData
import com.wordwell.libwwmw.domain.models.Word
import com.wordwell.libwwmw.domain.models.WordSet
import com.wordwell.libwwmw.presentation.viewmodels.CachedWordsViewModel
import com.wordwell.libwwmw.presentation.viewmodels.UiState
import com.wordwell.libwwmw.presentation.viewmodels.WordDetailViewModel
import com.wordwell.libwwmw.utils.Constants
import com.wordwell.libwwmw.utils.LogUtils
import kotlinx.coroutines.launch

class DemoActivity : AppCompatActivity() {
    private lateinit var container: WordWellServer
    private lateinit var wordViewModel: WordDetailViewModel
    private lateinit var cachedWordsViewModel: CachedWordsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize container with your API key
        container = WordWellServer.getInstance(
            context = applicationContext,
            apiKey = Constants.MW_API_KEY,
            useMockApi = false  // Use mock API instead of real network calls
        )

        val factory = container.wordDetailViewModelFactory
        wordViewModel = ViewModelProvider(this, factory)[WordDetailViewModel::class.java]
        cachedWordsViewModel = ViewModelProvider(this, container.cachedWordsViewModelFactory)[CachedWordsViewModel::class.java]
        val words = listOf( "tiger", "fox", "elephant", "lion", "giraffe",
            "zebra", "rabbit", "dog", "cat", "horse",
            "monkey", "bear", "panda", "kangaroo", "squirrel",
            "deer", "dolphin", "shark", "whale", "penguin",
            "octopus", "snail", "frog", "wolf", "bat")

       cachedWordsViewModel.fetchWordsBySetName("animals", words)

        cachedWordsViewModel.fetchAllSets()

        lifecycleScope.launch {

            cachedWordsViewModel.uiState.collect { state ->
                when (state) {
                    is UiState.Success ->
                        with(state.data) {
                            if (this is WWResultData.WordResult)
                                LogUtils.log("Words:$this")
                            else if (this is WWResultData.WordSetResult)
                                LogUtils.log("Sets:$this")
                        }
                    else -> {}
                }
            }
        }

        // Only call this once
        //wordViewModel.lookupWord("hello")
        
        // Use lifecycleScope and repeatOnLifecycle for proper lifecycle management
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                wordViewModel.uiState.collect { state ->
                    // Handle state updates
                    when (state) {
                        is UiState.Success -> {
                            LogUtils.log(state.data.toString())
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clear the instance when the activity is destroyed
        WordWellServer.clearInstance()
    }
}
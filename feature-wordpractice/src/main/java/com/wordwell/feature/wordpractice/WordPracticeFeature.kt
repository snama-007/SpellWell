package com.wordwell.feature.wordpractice

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.wordwell.feature.wordpractice.presentation.WordPracticeFragment
import com.wordwell.libwwmw.WordWellServer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WordPracticeFeature @Inject constructor(
    @ApplicationContext private val context: Context,
    private val wordWellServer: WordWellServer
) {
    fun getStartFragment(): Fragment {
        return WordPracticeFragment()
    }

    fun getNavHostFragment(): NavHostFragment {
        return NavHostFragment.create(R.navigation.word_practice_nav_graph)
    }

    companion object {
        const val FEATURE_NAME = "word_practice"
        val NAV_GRAPH_ID = R.navigation.word_practice_nav_graph
    }
} 
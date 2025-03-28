package com.wordwell.app

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.wordwell.app.util.MockWordsData
import com.wordwell.feature.wordpractice.Utils.PermissionHelper
import com.wordwell.feature.wordpractice.WordPracticeFeature
import com.wordwell.feature.wordsearch.WordSearchFeature
import com.wordwell.libwwmw.WordWellServer
import com.wordwell.libwwmw.presentation.viewmodels.CachedWordsViewModel
import com.wordwell.libwwmw.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject 
    lateinit var wordPracticeFeature: WordPracticeFeature
    
    @Inject
    lateinit var wordSearchFeature: WordSearchFeature
    
    private lateinit var container: WordWellServer
    private lateinit var cachedWordsViewModel: CachedWordsViewModel
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadMockData()
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            // Set up the SearchFeature in the search_container
            if (findViewById<androidx.compose.ui.platform.ComposeView>(R.id.search_container) != null) {
                findViewById<androidx.compose.ui.platform.ComposeView>(R.id.search_container).setContent {
                    wordSearchFeature.SearchUI()
                }
            }
            
            // Create NavHostFragment
            navHostFragment = wordPracticeFeature.getNavHostFragment()
            
            // Add NavHostFragment to container
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_container, navHostFragment)
                .addToBackStack(null)
                .setPrimaryNavigationFragment(navHostFragment)
                .commit()
        } else {
            // Restore NavHostFragment from savedInstanceState
            navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        }

        checkAndRequestNotificationPermission()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onNavigateUp(): Boolean {
        return navHostFragment.navController.navigateUp() || super.onNavigateUp()

    }

    @Deprecated("kotlin")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (!PermissionHelper.checkNotificationPermission(this)) {
            if (PermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Show rationale if needed
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Notifications help you stay updated with your learning progress",
                    Snackbar.LENGTH_LONG
                ).setAction("Grant") {
                    PermissionHelper.requestNotificationPermission(this)
                }.show()
            } else {
                PermissionHelper.requestNotificationPermission(this)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now show notifications
            } else {
                // Permission denied, handle accordingly
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Notifications are disabled. You can enable them in settings.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun loadMockData(){
        container = WordWellServer.getInstance(
            context = applicationContext,
            apiKey = Constants.MW_API_KEY
        )
        val factory = container.cachedWordsViewModelFactory
        cachedWordsViewModel = ViewModelProvider(this, factory)[CachedWordsViewModel::class.java]

        MockWordsData.wordSetsHashMap.forEach { key, value ->
            cachedWordsViewModel.fetchWordsBySetName(key, value)
            runBlocking{delay(300)}
        }
    }
}
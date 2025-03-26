package com.wordwell.app

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.wordwell.app.util.MockWordsData
import com.wordwell.app.util.NotificationPermissionHelper
import com.wordwell.feature.wordpractice.WordPracticeFeature
import com.wordwell.libwwmw.WordWellServer
import com.wordwell.libwwmw.presentation.viewmodels.CachedWordsViewModel
import com.wordwell.libwwmw.presentation.viewmodels.WordDetailViewModel
import com.wordwell.libwwmw.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject 
    lateinit var wordPracticeFeature: WordPracticeFeature
    private lateinit var container: WordWellServer
    private lateinit var cachedWordsViewModel: CachedWordsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadMockData()
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            // Create NavHostFragment
            val navHostFragment = wordPracticeFeature.getNavHostFragment()
            
            // Add NavHostFragment to container
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_container, navHostFragment)
                .setPrimaryNavigationFragment(navHostFragment) // this is important for proper navigation
                .commit()
        }

        checkAndRequestNotificationPermission()
    }

    private fun checkAndRequestNotificationPermission() {
        if (!NotificationPermissionHelper.checkNotificationPermission(this)) {
            if (NotificationPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Show rationale if needed
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Notifications help you stay updated with your learning progress",
                    Snackbar.LENGTH_LONG
                ).setAction("Grant") {
                    NotificationPermissionHelper.requestNotificationPermission(this)
                }.show()
            } else {
                NotificationPermissionHelper.requestNotificationPermission(this)
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
package com.wordwell.feature.wordsearch.voice

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Helper class for audio permission handling
 */
object PermissionHelper {
    
    const val RECORD_AUDIO_REQUEST_CODE = 1001
    
    /**
     * Checks if audio recording permission is granted
     */
    fun hasAudioPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Requests audio recording permission
     */
    fun requestAudioPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            RECORD_AUDIO_REQUEST_CODE
        )
    }
    
    /**
     * Checks if we should show rationale for audio permission
     */
    fun shouldShowAudioPermissionRationale(activity: Activity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity, 
            Manifest.permission.RECORD_AUDIO
        )
    }
} 
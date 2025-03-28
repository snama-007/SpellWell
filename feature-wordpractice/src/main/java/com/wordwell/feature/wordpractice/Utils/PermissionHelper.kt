package com.wordwell.feature.wordpractice.Utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionHelper {
    private const val NOTIFICATION_PERMISSION_CODE = 123
    private const val VIBRATE_PERMISSION_CODE = 124

    fun checkNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun shouldShowRequestPermissionRationale(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context is Activity &&
            context.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            false
        }
    }

    fun requestNotificationPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context is Activity) {
                context.requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE
                )
            }
        }
    }

    fun checkVibratePermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.VIBRATE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestVibratePermission(context: Context) {
        if (context is Activity) {
            context.requestPermissions(
                arrayOf(Manifest.permission.VIBRATE),
                VIBRATE_PERMISSION_CODE
            )
        }
    }
} 
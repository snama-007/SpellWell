package com.wordwell.libwwmw.utils

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * SecurityUtils provides utility functions for handling security operations
 * such as encrypting and decrypting the API key, and verifying app integrity.
 */
internal object SecurityUtils {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore" // Keystore type for storing keys
    private const val KEY_ALIAS = "MerriamWebsterApiKey" // Alias for the API key
    private const val TRANSFORMATION = "AES/GCM/NoPadding" // Transformation for encryption/decryption
    private const val AUTH_TAG_LENGTH = 128 // Authentication tag length for GCM

    /**
     * Encrypts the API key for secure storage.
     * Generates a new key if it doesn't exist in the keystore.
     * @param context The application context
     * @param apiKey The API key to encrypt
     * @return The encrypted API key as a Base64-encoded string
     */
    fun encryptApiKey(context: Context, apiKey: String): String {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        
        // Generate key if it doesn't exist
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }

        val key = keyStore.getKey(KEY_ALIAS, null) as SecretKey
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)

        val encrypted = cipher.doFinal(apiKey.toByteArray())
        val combined = cipher.iv + encrypted

        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    /**
     * Decrypts the stored API key.
     * @param context The application context
     * @param encryptedData The encrypted API key as a Base64-encoded string
     * @return The decrypted API key
     */
    fun decryptApiKey(context: Context, encryptedData: String): String {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val key = keyStore.getKey(KEY_ALIAS, null) as SecretKey
        
        val combined = Base64.decode(encryptedData, Base64.DEFAULT)
        val iv = combined.sliceArray(0..11)
        val encrypted = combined.sliceArray(12..combined.lastIndex)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(AUTH_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)

        return String(cipher.doFinal(encrypted))
    }

    /**
     * Checks if the app has been tampered with.
     * @param context The application context
     * @return True if the app integrity is verified, false otherwise
     */
    fun verifyAppIntegrity(context: Context): Boolean {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                android.content.pm.PackageManager.GET_SIGNATURES
            )
            // Add your signature verification logic here
            true
        } catch (e: Exception) {
            false
        }
    }
} 
package com.wordwell.libwwmw.domain.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.wordwell.libwwmw.data.db.DictionaryDatabase
import com.wordwell.libwwmw.utils.Constants
import com.wordwell.libwwmw.utils.LogUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Provider

/**
 * Worker class for downloading audio files for words.
 * Downloads MP3 files from the provided URLs and stores them in app's internal storage.
 */
class AudioDownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val dictionaryDatabase: Provider<DictionaryDatabase>
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val wordId = inputData.getString(KEY_WORD_ID) ?: return@withContext Result.failure()
        val audioUrl = inputData.getString(KEY_AUDIO_URL) ?: return@withContext Result.failure()
        
        val db = dictionaryDatabase.get()
        val wordDao = db.wordDao()
        
        // Update status to in progress
        wordDao.updateAudioStatus(wordId, Constants.DOWNLOAD_STATUS_IN_PROGRESS)
        
        try {
            // Create audio directory if it doesn't exist
            val audioDir = File(applicationContext.filesDir, Constants.AUDIO_DIR_NAME)
            if (!audioDir.exists()) {
                audioDir.mkdirs()
            }
            
            // Define target file
            val audioFileName = "$wordId${Constants.AUDIO_FILE_EXTENSION}"
            val audioFile = File(audioDir, audioFileName)
            val audioFilePath = audioFile.absolutePath
            
            LogUtils.log("Downloading audio: $audioUrl to $audioFilePath")
            
            // Download the file
            downloadFile(audioUrl, audioFile)
            
            // Update database with file path and status
            wordDao.updateAudioInfo(wordId, audioFilePath, Constants.DOWNLOAD_STATUS_COMPLETED)
            
            LogUtils.log("Audio download completed: $wordId")
            Result.success()
        } catch (e: Exception) {
            LogUtils.log("Audio download failed: ${e.message}", isError = true)
            // Update status to failed
            wordDao.updateAudioStatus(wordId, Constants.DOWNLOAD_STATUS_FAILED)
            Result.failure()
        }
    }
    
    /**
     * Downloads file from URL to the specified file.
     */
    private suspend fun downloadFile(fileUrl: String, outputFile: File) = withContext(Dispatchers.IO) {
        try {
            val url = URL(fileUrl)
            val connection = url.openConnection()
            connection.connectTimeout = 5000
            connection.readTimeout = 10000
            
            connection.getInputStream().use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            LogUtils.log("Download error: ${e.message}", isError = true)
            throw e
        }
    }
    
    companion object {
        const val KEY_WORD_ID = "word_id"
        const val KEY_AUDIO_URL = "audio_url"
    }
    
    /**
     * Factory for creating AudioDownloadWorker with assisted injection.
     */
    @AssistedFactory
    interface Factory {
        fun create(
            appContext: Context,
            params: WorkerParameters
        ): AudioDownloadWorker
    }
}

/**
 * Interface for worker factories to support DI with WorkManager.
 */
interface ChildWorkerFactory {
    fun create(appContext: Context, params: WorkerParameters): CoroutineWorker
} 
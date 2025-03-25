package com.wordwell.libwwmw.domain.audio

import android.content.Context
import com.ketch.Ketch
import com.ketch.Status
import com.wordwell.libwwmw.data.db.DictionaryDatabase
import com.wordwell.libwwmw.data.db.entities.WordEntity
import com.wordwell.libwwmw.domain.models.DictionaryFetchResult
import com.wordwell.libwwmw.domain.models.Word
import com.wordwell.libwwmw.utils.Constants
import com.wordwell.libwwmw.utils.LogUtils
import com.wordwell.libwwmw.utils.UrlUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for handling audio file downloads.
 * Provides methods to queue, schedule, and manage audio downloads.
 */
@Singleton
class AudioDownloadManager @Inject constructor(
    context: Context,
    dictionaryDatabase: DictionaryDatabase,
    private val coroutineScope: CoroutineScope
) {
    private val wordDao = dictionaryDatabase.wordDao()
    private val ketchDownloader: Ketch = Ketch.builder().build(context)
    private val contextWeakRef = WeakReference(context)
    
    // Track active downloads to prevent duplicates
    private val activeDownloads = ConcurrentHashMap<String, Boolean>()

    /**
     * Schedules a single audio download and returns a flow of the download progress.
     * This method launches the download in a separate coroutine to allow concurrent downloads.
     * 
     * @param wordId ID of the word
     * @param audioUrl URL to the audio file
     * @return Flow emitting the result of the download operation
     */
    suspend fun scheduleAudioDownload(wordId: String, audioUrl: String): Flow<DictionaryFetchResult<Word>> = flow {
        LogUtils.log("Scheduling audio download for word: $wordId, url: $audioUrl")
        
        // Skip if this word is already being downloaded
        if (activeDownloads.putIfAbsent(wordId, true) != null) {
            LogUtils.log("Download already in progress for word: $wordId")
            return@flow
        }
        
        try {
            // Update status to in progress
            wordDao.updateAudioStatus(wordId, Constants.DOWNLOAD_STATUS_IN_PROGRESS)
            
            // Launch the actual download in a separate coroutine
            coroutineScope.async(Dispatchers.IO) {
                LogUtils.log("Launching audio download for word: $wordId")
                downloadAudio(wordId, audioUrl)
            }
            
            // Emit initial status immediately and return
            val initialWord = wordDao.getWord(wordId)
            initialWord.collect {
                it?.let{ emit(DictionaryFetchResult.Success(it.toWord())) }
            }
        } catch (e: Exception) {
            LogUtils.log("Failed to schedule audio download: ${e.message}", isError = true)
            activeDownloads.remove(wordId)
            emit(DictionaryFetchResult.Error("Failed to schedule audio download: ${e.message}", e))
        }
    }
    
    /**
     * Performs the actual download operation in a background coroutine.
     * This is separated from scheduleAudioDownload to allow non-blocking scheduling.
     */
    private suspend fun downloadAudio(wordId: String, audioUrl: String) {
        try {
            // Create audio directory if it doesn't exist
            val context = contextWeakRef.get() ?: throw IllegalStateException("Context is null")
            val audioDir = File(context.filesDir, Constants.AUDIO_DIR_NAME)
            if (!audioDir.exists()) {
                audioDir.mkdirs()
            }

            // Define target file
            val audioFileName = UrlUtils.extractFileNameWithExtension(audioUrl, Constants.AUDIO_FILE_EXTENSION) 
                ?: "$wordId.${Constants.AUDIO_FILE_EXTENSION}"
            
            val audioFile = File(audioDir, audioFileName)
            val audioFilePath = audioFile.absolutePath
            LogUtils.log("Downloading audio: $audioUrl to $audioFilePath")

            // Start the download using Ketch
            val downloadId = ketchDownloader.download(audioUrl, audioDir.path, audioFileName)
            
            // Monitor download progress
            ketchDownloader.observeDownloadById(downloadId).collect { download ->
                when (download?.status) {
                    Status.SUCCESS -> {
                        // Update database with file path and status
                        wordDao.updateAudioInfo(wordId, audioFilePath, Constants.DOWNLOAD_STATUS_COMPLETED)
                        LogUtils.log("Audio download completed: $wordId")
                    }
                    Status.FAILED -> {
                        // Update status to failed
                        wordDao.updateAudioStatus(wordId, Constants.DOWNLOAD_STATUS_FAILED)
                        LogUtils.log("Audio download failed for $wordId", isError = true)
                    }
                    else -> {
                        // Still in progress
                    }
                }
            }
        } catch (e: Exception) {
            LogUtils.log("Audio download failed: ${e.message}", isError = true)
            // Update status to failed
            wordDao.updateAudioStatus(wordId, Constants.DOWNLOAD_STATUS_FAILED)
        } finally {
            // Always remove from active downloads when done
            activeDownloads.remove(wordId)
        }
    }

    /**
     * Processes pending audio downloads from the database.
     * Downloads are started concurrently up to the specified limit.
     * 
     * @param limit Maximum number of downloads to process at once
     */
    fun processPendingDownloads(limit: Int = 10) {
        coroutineScope.launch(Dispatchers.IO) {
            val pendingWords = wordDao.getWordsWithPendingAudio(limit)
            LogUtils.log("Processing ${pendingWords.size} pending audio downloads")
            // Launch each download in its own coroutine
            pendingWords.forEachIndexed { index, wordEntity ->
                wordEntity.audioUrl?.let { url ->
                    LogUtils.log("Processing pending audio download for word: ${wordEntity.id}")
                    scheduleAudioDownload(wordEntity.id, url)
                }

                if (index == 0) return@launch

            }
        }
    }
    
    /**
     * Automatically schedules audio downloads for a word if it has audio URLs.
     * @param word The word to check and schedule
     */
    fun checkAndQueueAudioDownload(wordEntity: WordEntity?) {
        if (wordEntity != null && !wordEntity.audioUrl.isNullOrBlank() && 
            wordEntity.audioDownloadStatus == Constants.DOWNLOAD_STATUS_PENDING) {
            
            coroutineScope.launch(Dispatchers.IO) {
                scheduleAudioDownload(wordEntity.id, wordEntity.audioUrl)
            }
        }
    }
    
    /**
     * Cancels all pending audio downloads.
     */
    fun cancelAllDownloads() {
        // Cancel all downloads in Ketch
        ketchDownloader.cancelAll()
        // Clear active downloads tracking
        activeDownloads.clear()
    }
    
    /**
     * Cancels a specific audio download.
     * @param wordId ID of the word to cancel download for
     */
    fun cancelDownload(wordId: String) {
        coroutineScope.launch(Dispatchers.IO) {
            // Remove from active downloads
            activeDownloads.remove(wordId)
            
            // Find and cancel the download
            wordDao.getWord(wordId).let { word ->
                word.collect {
                    it?.let { wordEntity ->
                        wordEntity.audioUrl?.let { url ->
                            ketchDownloader.cancel(url)
                        }
                    }
                }
            }
        }
    }
}
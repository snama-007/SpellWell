package com.wordwell.libwwmw.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.wordwell.libwwmw.data.api.MerriamWebsterApi
import com.wordwell.libwwmw.data.api.models.DictionaryResponse
import com.wordwell.libwwmw.data.db.DictionaryDatabase
import com.wordwell.libwwmw.data.db.dao.WordDao
import com.wordwell.libwwmw.data.db.entities.WordEntity
import com.wordwell.libwwmw.domain.models.Definition
import com.wordwell.libwwmw.domain.models.DictionaryFetchResult
import com.wordwell.libwwmw.domain.models.Phonetic
import com.wordwell.libwwmw.domain.models.Word
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class DictionaryRepositoryImplTest {

    @Mock private lateinit var api: MerriamWebsterApi
    @Mock private lateinit var db: DictionaryDatabase
    @Mock private lateinit var context: Context
    @Mock private lateinit var wordDao: WordDao
    @Mock private lateinit var connectivityManager: ConnectivityManager
    @Mock private lateinit var networkCapabilities: NetworkCapabilities

    private lateinit var repository: DictionaryRepositoryImpl
    private val apiKey = "test_api_key"

    private val testWord = Word(
        id = "test_id",
        word = "test",
        phonetics = listOf(Phonetic("test", "audio_url")),
        definitions = listOf(Definition("noun", "a test word", listOf("example"))),
        timestamp = 123L
    )

    private val testWordEntity = WordEntity(
        id = testWord.id,
        word = testWord.word,
        phonetics = testWord.phonetics,
        definitions = testWord.definitions,
        timestamp = testWord.timestamp
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(db.wordDao()).thenReturn(wordDao)
        `when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        `when`(connectivityManager.activeNetwork).thenReturn(mock())
        `when`(connectivityManager.getNetworkCapabilities(any())).thenReturn(networkCapabilities)
        
        repository = DictionaryRepositoryImpl(api, db, context, apiKey)
    }

    @Test
    fun `getWord returns cached data when available`() = runTest {
        // Given
        `when`(wordDao.getWord("test")).thenReturn(flowOf(testWordEntity))
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(false)

        // When
        val result = repository.getWord("test")

        // Then
        val emissions = result.first()
        assertTrue(emissions is DictionaryFetchResult.Success)
        assertEquals(testWord, (emissions as DictionaryFetchResult.Success).data)
    }

    @Test
    fun `getWord fetches from API when online and updates cache`() = runTest {
        // Given
        val apiResponse = mock<DictionaryResponse>()
        `when`(wordDao.getWord("test")).thenReturn(flowOf(null))
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
        `when`(api.getWord("test")).thenReturn(listOf(apiResponse))
        
        // When
        val result = repository.getWord("test")

        // Then
        verify(api).getWord("test")
        verify(wordDao).insertWord(any())
        verify(wordDao).keepRecentWords()
    }

    @Test
    fun `getWord returns error when offline and no cache`() = runTest {
        // Given
        `when`(wordDao.getWord("test")).thenReturn(flowOf(null))
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(false)

        // When
        val result = repository.getWord("test")

        // Then
        val emissions = result.first()
        assertTrue(emissions is DictionaryFetchResult.Error)
        assertEquals(
            "No internet connection and no cached data",
            (emissions as DictionaryFetchResult.Error).message
        )
    }

    @Test
    fun `getCachedWords returns mapped domain models`() = runTest {
        // Given
        `when`(wordDao.getAllWords()).thenReturn(flowOf(listOf(testWordEntity)))

        // When
        val result = repository.getCachedWords()

        // Then
        val words = result.first()
        assertEquals(1, words.size)
        assertEquals(testWord, words[0])
    }

    @Test
    fun `clearCache calls dao clearWords`() = runTest {
        // When
        repository.clearCache()

        // Then
        verify(wordDao).clearWords()
    }

    @Test
    fun `getCacheSize returns dao word count`() = runTest {
        // Given
        `when`(wordDao.getWordCount()).thenReturn(42)

        // When
        val result = repository.getCacheSize()

        // Then
        assertEquals(42, result)
    }
} 
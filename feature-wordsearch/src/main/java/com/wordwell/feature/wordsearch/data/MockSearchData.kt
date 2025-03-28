package com.wordwell.feature.wordsearch.data

import com.wordwell.feature.wordsearch.domain.model.SearchResult

/**
 * Provides mock data for the search feature
 */
object MockSearchData {
    
    // Mock suggestions based on categories for kids K-8
    val defaultSuggestions = listOf(
        "red flowers",
        "animal words",
        "kitchen tools",
        "space objects",
        "superhero names",
        "ocean creatures",
        "weather words",
        "dinosaur names"
    )
    
    // Category-based suggestions
    val categorizedSuggestions = mapOf(
        "animals" to listOf("farm animals", "zoo animals", "sea creatures", "insects", "birds"),
        "nature" to listOf("flowers", "trees", "weather", "seasons", "landscapes"),
        "food" to listOf("fruits", "vegetables", "desserts", "breakfast foods", "snacks"),
        "school" to listOf("classroom items", "subjects", "school places", "stationery"),
        "fantasy" to listOf("magic words", "fairy tale characters", "mythical creatures")
    )
    
    // Mock word results
    val mockWordResults = listOf(
        SearchResult.WordResult(
            id = "w1",
            word = "elephant",
            definition = "A very large animal with thick grey skin, large ears, two curved outer teeth called tusks and a long nose called a trunk.",
            partOfSpeech = "noun",
            phoneticSpelling = "ˈel·ə·fənt"
        ),
        SearchResult.WordResult(
            id = "w2",
            word = "butterfly",
            definition = "An insect with large, often colorful wings and a thin body.",
            partOfSpeech = "noun",
            phoneticSpelling = "ˈbə·tər·flī"
        ),
        SearchResult.WordResult(
            id = "w3",
            word = "telescope",
            definition = "An instrument shaped like a tube that makes distant objects appear larger and closer when you look through it.",
            partOfSpeech = "noun",
            phoneticSpelling = "ˈtel·ə·skōp"
        ),
        SearchResult.WordResult(
            id = "w4",
            word = "rainbow",
            definition = "An arch of colors formed in the sky caused by the sun shining through rain.",
            partOfSpeech = "noun",
            phoneticSpelling = "ˈrān·bō"
        )
    )
    
    // Mock set results
    val mockSetResults = listOf(
        SearchResult.SetResult(
            id = "s1",
            name = "Animal Kingdom",
            description = "Learn about different types of animals",
            wordCount = 25,
            category = "animals"
        ),
        SearchResult.SetResult(
            id = "s2",
            name = "Space Explorers",
            description = "Words about space and astronomy",
            wordCount = 20,
            category = "science"
        ),
        SearchResult.SetResult(
            id = "s3",
            name = "Kitchen Vocabulary",
            description = "Words you can find in a kitchen",
            wordCount = 18,
            category = "house"
        ),
        SearchResult.SetResult(
            id = "s4",
            name = "Superhero Words",
            description = "Words related to superheroes and their powers",
            wordCount = 22,
            category = "fantasy"
        )
    )
    
    // Method to get search results based on query and mode
    fun getSearchResults(query: String, isWordSetMode: Boolean): List<SearchResult> {
        // Simple filtering logic for mock data
        val lowercaseQuery = query.lowercase()
        
        return if (isWordSetMode) {
            mockSetResults.filter { 
                it.name.lowercase().contains(lowercaseQuery) || 
                it.description.lowercase().contains(lowercaseQuery) ||
                it.category.lowercase().contains(lowercaseQuery)
            }
        } else {
            mockWordResults.filter {
                it.word.lowercase().contains(lowercaseQuery) ||
                it.definition.lowercase().contains(lowercaseQuery)
            }
        }
    }
    
    // Get suggestions based on query and recent user interactions
    fun getSuggestions(query: String): List<String> {
        if (query.isBlank()) {
            return defaultSuggestions
        }
        
        val lowerQuery = query.lowercase()
        val allSuggestions = defaultSuggestions + categorizedSuggestions.values.flatten()
        
        return allSuggestions.filter { it.lowercase().contains(lowerQuery) }
            .take(8) // Limit to 8 suggestions
    }
} 
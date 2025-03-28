package com.wordwell.app.util

object MockWordsData {
    val wordSetsHashMap: HashMap<String, List<String>> = hashMapOf(
        "Animals" to listOf(
            "tiger", "fox", "elephant", "lion", "giraffe",
            "zebra", "rabbit", "dog", "cat", "horse",
            "monkey", "bear", "panda", "kangaroo", "squirrel",
            "deer", "dolphin", "shark", "whale", "penguin",
            "octopus", "snail", "frog", "wolf", "bat"
        ),
        "Fruits" to listOf(
            "apple", "banana", "orange", "grape", "mango",
            "pineapple", "watermelon", "strawberry", "blueberry", "cherry",
            "peach", "plum", "kiwi", "lemon", "lime",
            "coconut", "pear", "pomegranate", "blackberry", "fig",
            "apricot", "guava", "papaya", "nectarine", "cranberry"
        ),
        "Colors" to listOf(
            "red", "blue", "green", "yellow", "orange",
            "purple", "pink", "brown", "black", "white",
            "gray", "violet", "indigo", "gold", "silver",
            "maroon", "magenta", "beige", "lavender",
            "peach", "navy", "teal", "turquoise", "salmon"
        ),
        "Nature" to listOf(
            "tree", "river", "mountain", "ocean", "lake",
            "flower", "cloud", "rain", "sun", "moon",
            "star", "forest", "valley", "hill", "grass",
            "rock", "sand", "breeze", "wind", "storm",
            "snow", "ice", "volcano", "cave", "desert"
        )
    )
}
# WordWell Library

A Kotlin Android library for integrating Merriam-Webster dictionary functionality with offline-first capabilities.

## Features

- Offline-first architecture
- Clean Architecture implementation
- Reactive data flow with Kotlin Flow
- Cache management (100 words limit)
- Modern Android development practices

## Architecture

The library follows Clean Architecture principles with three main layers:

```
├── data/
│   ├── api/      # Network calls
│   ├── db/       # Local storage
│   └── repository/ # Data management
├── domain/
│   ├── models/   # Business models
│   ├── repository/ # Abstract definitions
│   └── usecases/ # Business logic
└── presentation/
    ├── viewmodels/ # UI state management
    └── ui/        # UI components
```

## Setup

1. Add the library to your project:

```groovy
dependencies {
    implementation project(':libwwmw')
}
```

2. Initialize with your API key:

```kotlin
val container = DictionaryContainer.getInstance(
    context = applicationContext,
    apiKey = "your-api-key"
)
```

## Usage

```kotlin
class YourActivity : AppCompatActivity() {
    private val viewModel: WordDetailViewModel by viewModels { 
        container.wordDetailViewModelFactory 
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is WordDetailUiState.Success -> showWordDetails(state.word)
                    is WordDetailUiState.Error -> showError(state.message)
                    is WordDetailUiState.Loading -> showLoading()
                    is WordDetailUiState.Initial -> Unit
                }
            }
        }
    }
}
```

## Dependencies

The library uses a version catalog for dependency management. Key dependencies include:

- Retrofit for networking
- Room for local storage
- Kotlin Coroutines and Flow
- AndroidX Lifecycle components

## License

[Add your license information here] 
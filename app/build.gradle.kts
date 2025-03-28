plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.navigation.safeargs)
}

android {
    namespace = "com.wordwell.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.wordwell.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        buildConfigField("String", "MERRIAM_WEBSTER_API_KEY", "\"${System.getenv("MERRIAM_WEBSTER_API_KEY") ?: ""}\"")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":libwwmw"))
    implementation(project(":feature-wordpractice"))
    implementation(project(":feature-wordsearch"))

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.bundles.lifecycle)
    implementation(libs.androidx.work.runtime.ktx)
    
    // Material Design
    implementation(libs.material)
    
    // Navigation
    implementation(libs.bundles.navigation)
    
    // Compose
    implementation(libs.androidx.activity.compose)

    
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)

    // Timber
    implementation(libs.jakewharton.timber)
}
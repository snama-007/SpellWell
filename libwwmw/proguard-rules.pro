# Keep API related classes
-keep class com.wordwell.libwwmw.data.api.** { *; }
-keep class com.wordwell.libwwmw.data.api.models.** { *; }

# Protect BuildConfig
-keep class com.wordwell.libwwmw.BuildConfig { *; }

# Protect Constants
-keepclassmembers class com.wordwell.libwwmw.utils.Constants {
    private static final String MERRIAM_WEBSTER_API_KEY;
}

# Retrofit rules
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp rules
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# Gson rules
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# General security rules
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/* 
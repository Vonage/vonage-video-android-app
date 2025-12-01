# ProGuard rules for Vonage Video Android App

# ================================================================================================
# General Configuration
# ================================================================================================

# Preserve line numbers for debugging stack traces
-keepattributes SourceFile,LineNumberTable

# Keep annotations, signatures, and metadata
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# ================================================================================================
# OpenTok/Vonage SDK
# ================================================================================================

-keep class com.opentok.android.** { *; }
-keep class com.vonage.webrtc.** { *; }

# ================================================================================================
# Moshi (JSON Serialization)
# ================================================================================================

# Keep data classes used with Moshi
-keep class com.vonage.android.data.network.** { *; }

# Keep classes annotated with @JsonClass
-keep @com.squareup.moshi.JsonClass class * { *; }
-keepclassmembers @com.squareup.moshi.JsonClass class * {
    <init>(...);
    <fields>;
}

# Keep generated JsonAdapter classes
-if @com.squareup.moshi.JsonClass class *
-keep class <1>JsonAdapter {
    <init>(...);
    <fields>;
}

# Keep Moshi core classes
-keep class com.squareup.moshi.** { *; }

# ================================================================================================
# Retrofit (HTTP Client)
# ================================================================================================

# Retain service method parameters
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Keep Retrofit interfaces
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep inherited Retrofit services
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# Keep response types for Retrofit methods
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# Keep Retrofit core classes
-keep,allowoptimization,allowshrinking,allowobfuscation class retrofit2.Response
-keep,allowobfuscation,allowshrinking interface retrofit2.Call

# ================================================================================================
# Kotlin Coroutines
# ================================================================================================

-keep,allowoptimization,allowshrinking,allowobfuscation class kotlin.coroutines.Continuation
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.** { *; }

# ================================================================================================
# Warnings to Ignore
# ================================================================================================

-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**

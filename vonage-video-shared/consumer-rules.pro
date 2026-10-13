# Keep the Retrofit service interface; consumers create it via Retrofit.create().
-keep,allowobfuscation interface com.vonage.android.shared.session.SessionApiService

# Keep kotlinx.serialization models used by the session data layer.
-keepclassmembers @kotlinx.serialization.Serializable class com.vonage.android.shared.session.** { *; }

# Keep ArchivingApi's method signatures so Retrofit can resolve generic return types
# (e.g. Response<StopArchivingResponse>) reflectively at runtime.
-keep,allowobfuscation interface com.vonage.android.archiving.data.ArchivingApi
-keepclassmembers interface com.vonage.android.archiving.data.ArchivingApi { *; }

# StopArchivingResponse is only referenced via ArchivingApi's generic return type, so R8
# can't see it's reachable and strips it as dead code without this rule.
-keepclassmembers @kotlinx.serialization.Serializable class com.vonage.android.archiving.data.** { *; }
-keep @kotlinx.serialization.Serializable class com.vonage.android.archiving.data.**

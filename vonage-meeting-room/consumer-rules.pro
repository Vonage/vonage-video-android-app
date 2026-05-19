# Keep all public API classes so they survive R8/ProGuard in consuming apps.
-keep public class com.vonage.android.meetingroom.api.** { *; }

# Keep Retrofit service interface declarations used by MeetingRoomApiService.
-keep,allowobfuscation interface com.vonage.android.meetingroom.internal.data.MeetingRoomApiService

# Keep kotlinx.serialization models used by the data layer.
-keepclassmembers @kotlinx.serialization.Serializable class com.vonage.android.meetingroom.internal.data.** { *; }

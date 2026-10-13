# Keep all public API classes so they survive R8/ProGuard in consuming apps.
-keep public class com.vonage.android.meetingroom.api.** { *; }

# The session Retrofit service and its models live in vonage-video-shared (com.vonage.android.shared.session),
# which ships its own consumer rules.

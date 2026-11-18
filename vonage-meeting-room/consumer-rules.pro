# Keep all public API classes so they survive R8/ProGuard in consuming apps.
-keep public class com.vonage.android.meetingroom.api.** { *; }

# The backend client (Retrofit services and their models) lives in vonage-vera-api-client
# (com.vonage.android.vera.api), which ships its own consumer rules.

# Keep the internal Retrofit service interfaces and their generic signatures, so Retrofit can
# resolve the response types (e.g. Response<TrpcResponse<CreateSessionResponse>>) reflectively.
-keep,allowobfuscation interface com.vonage.android.vera.api.**.*Service
-keepclassmembers interface com.vonage.android.vera.api.**.*Service { *; }

# Request/response models are only reachable through those generic signatures, so R8 would
# strip them as dead code without these rules.
-keepclassmembers @kotlinx.serialization.Serializable class com.vonage.android.vera.api.** { *; }
-keep @kotlinx.serialization.Serializable class com.vonage.android.vera.api.**

# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keeppackagenames
-keep class com.opentok.** { *; }
-keep class com.vonage.** { *; }

-dontwarn com.google.common.flogger.FluentLogger$Api
-dontwarn com.google.common.flogger.FluentLogger
-dontwarn com.google.common.flogger.LoggingApi
-dontwarn com.google.protobuf.GeneratedMessageLite$Builder
-dontwarn com.google.protobuf.GeneratedMessageLite$MethodToInvoke
-dontwarn com.google.protobuf.GeneratedMessageLite
-dontwarn com.google.protobuf.Internal$EnumLite
-dontwarn com.google.protobuf.Internal$EnumLiteMap
-dontwarn com.google.protobuf.Internal$EnumVerifier
-dontwarn com.google.protobuf.Internal$FloatList
-dontwarn com.google.protobuf.Internal$IntList
-dontwarn com.google.protobuf.Internal$ProtobufList
-dontwarn com.google.protobuf.MessageLiteOrBuilder

-dontwarn com.vonage.android.kotlin.VonageVideoClient
-dontwarn com.vonage.android.kotlin.ext.**
-dontwarn com.vonage.android.kotlin.internal.**
-dontwarn com.vonage.android.kotlin.model.**
-dontwarn com.vonage.android.kotlin.signal.**

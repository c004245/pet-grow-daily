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

# Remote Config models
-keep class kr.co.hyunwook.pet_grow_daily.core.database.entity.OrderProduct { *; }
-keep class kr.co.hyunwook.pet_grow_daily.core.database.entity.OrderProductListModel { *; }
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Kakao SDK ProGuard rules
-keep class com.kakao.** { *; }
-keep class com.kakao.sdk.** { *; }
-keep class com.kakao.auth.** { *; }
-keep class com.kakao.network.** { *; }
-keep class com.kakao.usermgmt.** { *; }
-keep class com.kakao.util.** { *; }
-dontwarn com.kakao.**

# Keep all classes that have @Keep annotation
-keep @androidx.annotation.Keep class *
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

# Keep retrofit and okhttp classes for network calls
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**

# Keep gson classes for JSON parsing
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**
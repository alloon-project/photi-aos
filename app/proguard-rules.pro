# Debugging: 스택 트레이스에 라인 번호 유지
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Data 모델 (Moshi/Gson JSON 파싱)
-keep class com.photi.aos.data.model.** { *; }

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Retrofit 서비스 인터페이스 유지
-keep interface com.photi.aos.data.remote.** { *; }
-keep class com.photi.aos.data.remote.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# Moshi
-keep class com.squareup.moshi.** { *; }
-keepclassmembers class ** {
    @com.squareup.moshi.FromJson *;
    @com.squareup.moshi.ToJson *;
}
-keep @com.squareup.moshi.JsonClass class * { *; }

# Gson
-keep class com.google.gson.** { *; }
-keepattributes *Annotation*

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class com.bumptech.glide.** { *; }
-dontwarn com.bumptech.glide.**

# Lottie
-dontwarn com.airbnb.lottie.**
-keep class com.airbnb.lottie.** { *; }

# Kakao SDK
-keep class com.kakao.** { *; }
-dontwarn com.kakao.**

# Google Sign-In / Credentials
-keep class com.google.android.libraries.identity.googleid.** { *; }
-keep class androidx.credentials.** { *; }
-dontwarn com.google.android.libraries.identity.googleid.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-dontwarn dagger.hilt.**

# DataBinding / ViewBinding
-keep class androidx.databinding.** { *; }
-dontwarn androidx.databinding.**

# Navigation
-keep class androidx.navigation.** { *; }

# ViewModel
-keep class com.photi.aos.viewmodel.** { *; }

# Repository
-keep class com.photi.aos.data.repository.** { *; }
-keep class com.photi.aos.data.storage.** { *; }

# Fragment / Activity (DataBinding 메서드 참조)
-keep class com.photi.aos.view.** { *; }
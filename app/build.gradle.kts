
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}
android {
    namespace = "com.photi.aos"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.photi.aos"
        minSdk = 26
        targetSdk = 35
        versionCode = 10
        versionName = "2.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        val localProperties =
            Properties().apply {
                val file = rootProject.file("local.properties")
                if (file.exists()) {
                    load(file.inputStream())
                }
            }

        val kakaoNativeAppKey = localProperties.getProperty("KAKAO_NATIVE_APP_KEY") ?: ""
        val googleWebClientId =  localProperties.getProperty("GOOGLE_WEB_CLIENT_ID") ?: ""

        buildConfigField(
            "String",
            "KAKAO_NATIVE_APP_KEY",
            "\"$kakaoNativeAppKey\"",
        )

        manifestPlaceholders["kakaoNativeAppKey"] = kakaoNativeAppKey

        buildConfigField(
            "String",
            "GOOGLE_WEB_CLIENT_ID",
            "\"$googleWebClientId\"",
        )
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.fragment:fragment-ktx:1.8.9")
    implementation("androidx.activity:activity-ktx:1.10.0")
    implementation("androidx.recyclerview:recyclerview:1.3.1")

    //material
    implementation("com.google.android.material:material:1.11.0")

    //instagram
    implementation ("com.facebook.android:facebook-android-sdk:latest.release")

    //paging
    implementation ("androidx.paging:paging-runtime:3.2.1")
    implementation ("androidx.paging:paging-runtime-ktx:3.2.1")

    //zigzag
    implementation("ir.beigirad:ZigzagView:1.2.1")

    //calendar
    implementation("com.github.prolificinteractive:material-calendarview:2.0.1")
    implementation("com.jakewharton.threetenabp:threetenabp:1.1.1")

    //glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.googleid)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.0")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //OKHttp
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.10.0"))

    //Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    annotationProcessor("com.google.dagger:hilt-compiler:2.44")

    //DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //Lottie
    implementation("com.airbnb.android:lottie:5.0.2")


    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.databinding.runtime)
    implementation(libs.androidx.navigation.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.kakao.sdk.user)

}
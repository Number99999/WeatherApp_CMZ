plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.cmzsoft.weather"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.cmzsoft.weather"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
//    defaultConfig {
//        applicationId = "com.cmzsoft.weather"
//        minSdk = 24
//        targetSdk = 35
//        versionCode = 1
//        versionName = "1.0"
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//    }

//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
//            )
//            buildConfigField(
//                "String",
//                "BANNER_AD_UNIT_ID",
//                "\"ca-app-pub-7340251527995818/9792653557\""
//            )
//            buildConfigField(
//                "String",
//                "INTERSTITIAL_AD_UNIT_ID",
//                "\"ca-app-pub-7340251527995818/6284894630\""
//            )
//            buildConfigField(
//                "String",
//                "APP_OPEN_AD_UNIT_ID",
//                "\"ca-app-pub-7340251527995818/7166490211\""
//            )
//            buildConfigField(
//                "String",
//                "NATIVE_AD_UNIT_ID",
//                "\"ca-app-pub-7340251527995818/5854468032\""
//            )
//        }
//        debug {
//            buildConfigField(
//                "String",
//                "BANNER_AD_UNIT_ID",
//                "\"ca-app-pub-3940256099942544/6300978111\""
//            )
//            buildConfigField(
//                "String",
//                "INTERSTITIAL_AD_UNIT_ID",
//                "\"ca-app-pub-3940256099942544/1033173712\""
//            )
//            buildConfigField(
//                "String",
//                "APP_OPEN_AD_UNIT_ID",
//                "\"ca-app-pub-3940256099942544/9257395921\""
//            )
//            buildConfigField(
//                "String",
//                "NATIVE_AD_UNIT_ID",
//                "\"ca-app-pub-3940256099942544/2247696110\""
//            )
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildToolsVersion = "35.0.0"
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-ads:24.3.0")
    implementation("androidx.fragment:fragment-ktx:1.4.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    implementation("com.google.ads.mediation:inmobi:10.8.3.1")
    implementation("com.google.ads.mediation:ironsource:8.9.0.0")
    implementation("com.google.ads.mediation:vungle:7.5.0.0")
    implementation("com.google.ads.mediation:mintegral:16.9.71.0")
    implementation("com.google.ads.mediation:pangle:7.2.0.3.0")
    implementation("com.google.ads.mediation:unity:4.15.0.0")
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-config")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.unity3d.ads:unity-ads:4.15.0")
    implementation("com.google.dagger:hilt-android:2.51")
    implementation(libs.play.services.maps)
    implementation(libs.protolite.well.known.types)
    implementation(libs.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
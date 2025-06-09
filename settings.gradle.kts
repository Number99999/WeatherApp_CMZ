pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        plugins {
            id("org.jetbrains.kotlin.kapt") version "1.9.23"
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // MPAndroidChart, Vungle,...
        maven { url = uri("https://artifact.bytedance.com/repository/pangle") } // Pangle
//        maven {
//            url = uri("https://android-sdk.mbridge.com/repository/mbridge_android_sdk_oversea")
//        } // Mintegral
        maven { url = uri("https://android-sdk.is.com/") } // ironSource
        maven { url = uri("https://repo.ironsrc.com/artifactory/mobile-sdk/") } // ironSource backup
        maven { url = uri("https://sdk.unityads.unity3d.com/release") } // Unity Ads
        maven { url = uri("https://maven.google.com") } // Google (backup)
    }
}

rootProject.name = "Weather"
include(":app")

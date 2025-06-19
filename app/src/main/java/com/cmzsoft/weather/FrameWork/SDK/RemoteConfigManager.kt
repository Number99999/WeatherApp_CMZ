package com.cmzsoft.weather.FrameWork.SDK

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.remoteConfig

class RemoteConfigManager private constructor() {

    private val mFirebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    private var remoteConfig: RemoteConfigModel = RemoteConfigModel(
        aoaEnabled = true,
        bannerEnabled = true,
        endTutorialEnabled = true,
        interDelayBackCategory = 60,
        interDelayCategorySec = 60,
        interDelayHomeSec = 60,
        interDelayItemChangeSec = 60,
        nativeAdsEnabled = true,
        rewardedEnabled = true
    )
    private val defaultConfigMap = mapOf(
        "example_key" to "default_value"
    )

    init {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600) // Tối thiểu 1 giờ mới fetch lại
            .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.setDefaultsAsync(defaultConfigMap)
    }

    fun getString(key: String): String {
        return mFirebaseRemoteConfig.getString(key)
    }

    fun getBoolean(key: String): Boolean {
        return mFirebaseRemoteConfig.getBoolean(key)
    }

    fun getLong(key: String): Long {
        return mFirebaseRemoteConfig.getLong(key)
    }

    fun fetchAndActivate(onComplete: (Boolean) -> Unit) {
        mFirebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateRemoteConfig(mFirebaseRemoteConfig)
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
    }

    fun updateRemoteConfig(remoteConfigInstance: FirebaseRemoteConfig) {
        println("fetch remote config")
        remoteConfig = RemoteConfigModel(
            aoaEnabled = remoteConfigInstance.getBoolean("aoa_enabled"),
            bannerEnabled = remoteConfigInstance.getBoolean("banner_enabled"),
            endTutorialEnabled = remoteConfigInstance.getBoolean("end_tutorial_enabled"),
            interDelayBackCategory = remoteConfigInstance.getLong("inter_delay_back_category")
                .toInt(),
            interDelayCategorySec = remoteConfigInstance.getLong("inter_delay_category_sec")
                .toInt(),
            interDelayHomeSec = remoteConfigInstance.getLong("inter_delay_home_sec").toInt(),
            interDelayItemChangeSec = remoteConfigInstance.getLong("inter_delay_item_change_sec")
                .toInt(),
            nativeAdsEnabled = remoteConfigInstance.getBoolean("nativeads_enabled"),
            rewardedEnabled = remoteConfigInstance.getBoolean("rewarded_enabled")
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: RemoteConfigManager? = null

        fun getInstance(): RemoteConfigManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RemoteConfigManager().also { INSTANCE = it }
            }
        }
    }

    fun fetchRemoteConfigCircle() {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        fetchAndActivate { }
    }

}


/* how to use
    val configManager = RemoteConfigManager.getInstance()

    // Gọi phương thức để thiết lập vòng lặp fetch
    configManager.fetchRemoteConfigCircle()

    // Tiến hành fetch và activate các giá trị từ Firebase
    configManager.fetchAndActivate { success ->
        if (success) {
            println("Remote Config values activated successfully.")
        } else {
            println("Failed to activate Remote Config values.")
        }
    }
 */
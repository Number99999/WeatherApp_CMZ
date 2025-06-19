package com.cmzsoft.weather.FrameWork.SDK

data class RemoteConfigModel(
    val aoaEnabled: Boolean,
    val bannerEnabled: Boolean,
    val endTutorialEnabled: Boolean,
    val interDelayBackCategory: Int,
    val interDelayCategorySec: Int,
    val interDelayHomeSec: Int,
    val interDelayItemChangeSec: Int,
    val nativeAdsEnabled: Boolean,
    val rewardedEnabled: Boolean
)
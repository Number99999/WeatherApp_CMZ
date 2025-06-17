package com.cmzsoft.weather

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.cmzsoft.weather.FrameWork.RemoteConfigManager
import com.cmzsoft.weather.Service.LocationService
import com.cmzsoft.weather.Utils.AppOpenAdManager
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class WeatherApp : Application(), Application.ActivityLifecycleCallbacks {
    private var activityReferences = 0
    private var isActivityChangingConfigurations = false
    private var isReturningFromBackground = false
    private var hasAppStartedOnce = false
    private var lastAdShownTime: Long = 0

    override fun onCreate() {
        super.onCreate()
//        MobileAds.initialize(this) {}
        MobileAds.initialize(
            this,
            OnInitializationCompleteListener { initializationStatus: InitializationStatus? ->
                Log.d("Mediation", "Google Mobile Ads SDK Initialized")
            })
        LocationService.init(this)
        registerActivityLifecycleCallbacks(this)
        RemoteConfigManager.getInstance().fetchRemoteConfigCircle()
        RemoteConfigManager.getInstance().fetchAndActivate { success ->
            if (success) {
                println("Remote Config values activated successfully.")
            } else {
                println("Failed to activate Remote Config values.")
            }
        }
    }

    override fun onActivityCreated(
        activity: Activity,
        savedInstanceState: Bundle?
    ) {
    }

    override fun onActivityStarted(activity: Activity) {
        val wasInBackground = activityReferences == 0

        activityReferences++

        if (wasInBackground && !isActivityChangingConfigurations && hasAppStartedOnce) {
            val currentTime = System.currentTimeMillis()
            val minInterval = 30 * 1000

            if (currentTime - lastAdShownTime > minInterval) {
                isReturningFromBackground = true
                lastAdShownTime = currentTime
            }
        }

        if (!hasAppStartedOnce) {
            hasAppStartedOnce = true
        }

        if (isReturningFromBackground) {
            isReturningFromBackground = false
//            AdLoadingDialogFragment.show(
//                (activity as AppCompatActivity).supportFragmentManager
//            )

            AppOpenAdManager.loadAd(
                context = this,
                onAdLoaded = {
                    AppOpenAdManager.showAdIfAvailable(activity, onAdImpression = {
                        onAdImpression()
                    }, onAdClosed = {

                    })
                },
                onAdFailed = {

                }
            )


        }
    }

    fun onAdClose() {
        println("Ad close")
        val changePage = Intent(this, ActivityRequestLocation::class.java);
        startActivity(changePage);
    }

    fun onAdImpression() {
        println("Ad onAdImpression")
    }


    override fun onActivityStopped(activity: Activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations
        activityReferences--
    }

    override fun onActivityResumed(activity: Activity) {

    }


    override fun onActivityPaused(activity: Activity) {
        //TODO("Not yet implemented")
    }

    override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle
    ) {
        //TODO("Not yet implemented")
    }

    override fun onActivityDestroyed(activity: Activity) {
        //TODO("Not yet implemented")
    }
}

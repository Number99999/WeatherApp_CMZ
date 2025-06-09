package com.cmzsoft.weather.Utils

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback

object AppOpenAdManager {
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isAdShowing = false

    fun loadAd(
        context: Context,
        onAdFailed: (() -> Unit)? = null,
        onAdLoaded: (() -> Unit)? = null
    ) {

        println("load ad");
        if (isLoadingAd || appOpenAd != null) return

        isLoadingAd = true
        val request = AdRequest.Builder().build()

        AppOpenAd.load(
            context,
            AdUnitIds.APP_OPEN, // Test ID
            request,
            object : AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    onAdLoaded?.invoke()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isLoadingAd = false
                    appOpenAd = null
                    onAdFailed?.invoke()
                }

            }
        )
    }

    fun showAdIfAvailable(activity: Activity, onAdClosed: () -> Unit, onAdImpression: () -> Unit) {
        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                isAdShowing = false
                appOpenAd = null
                onAdClosed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                isAdShowing = false
                onAdClosed()
            }

            override fun onAdImpression() {
                super.onAdImpression()
                onAdImpression()
            }
        }
        appOpenAd?.show(activity)
    }
}

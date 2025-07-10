package com.boom.weather.Manager

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.boom.weather.R
import com.boom.weather.Utils.AdUnitIds
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var interstitialAd: InterstitialAd? = null
    private val lastAdShownTimeMap: MutableMap<String, Long> = mutableMapOf()
    private val isFirstAdShownMap: MutableMap<String, Boolean> = mutableMapOf()

    companion object {
        private var INSTANCE: AdManager? = null

        fun getInstance(context: Context): AdManager {
            if (INSTANCE == null) {
                INSTANCE = AdManager(context)
            }
            return INSTANCE!!
        }
    }

    fun loadInterstitialAdIfNeeded(activity: Activity) {
        println("loadInterstitialAdIfNeeded")
        if (interstitialAd != null || !isActivityValid(activity)) return

        InterstitialAd.load(
            activity,
//            AdUnitIds.INTERSTITIAL,
            "ca-app-pub-3940256099942544/1033173712",
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    println("onAdLoaded")
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    println("onAdFailedToLoad")
                    interstitialAd = null
                }
            })
    }

    fun showInterstitialAdIfEligible(
        activity: Activity,
        adTag: String, // unique key per screen
        minIntervalMillis: Long,
        onAdStartShowing: () -> Unit = {},
        onAdClosed: () -> Unit,
        onAdSkipped: () -> Unit,
        onAdImpression: () -> Unit,
        onAdFailedToShow: (String) -> Unit = {}
    ) {
        print("showInterstitialAdIfEligible")
        val now = SystemClock.elapsedRealtime()
        val lastShown = lastAdShownTimeMap[adTag] ?: 0L
        val isFirst = isFirstAdShownMap[adTag] != false
        val canShow = isFirst || now - lastShown >= minIntervalMillis

        if (!canShow) {
            onAdSkipped()
            return
        }

        showInterstitialAd(
            activity = activity, onAdStartShowing = onAdStartShowing, onAdClosed = {
                lastAdShownTimeMap[adTag] = SystemClock.elapsedRealtime()
                isFirstAdShownMap[adTag] = false
                loadInterstitialAdIfNeeded(activity)
                onAdClosed()
            }, onAdFailedToShow = {
                loadInterstitialAdIfNeeded(activity)
                onAdFailedToShow(it)
            }, onAdImpression = onAdImpression
        )
    }

    private fun showInterstitialAd(
        activity: Activity,
        onAdStartShowing: () -> Unit,
        onAdClosed: () -> Unit,
        onAdImpression: () -> Unit,
        onAdFailedToShow: (String) -> Unit,
    ) {
        if (!isActivityValid(activity)) {
            onAdFailedToShow("Activity invalid")
            return
        }

        interstitialAd?.let { ad ->
            onAdStartShowing()
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    onAdFailedToShow(adError.message)
                }

                override fun onAdShowedFullScreenContent() {
                    interstitialAd = null
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    onAdImpression()
                }
            }

            ad.show(activity)
        } ?: run {
            loadAndShowInterstitialAd(
                activity, onAdStartShowing, onAdClosed, onAdFailedToShow, onAdImpression
            )
        }
    }

    private fun loadAndShowInterstitialAd(
        activity: Activity,
        onAdStartShowing: () -> Unit,
        onAdClosed: () -> Unit,
        onAdFailedToShow: (String) -> Unit,
        onAdImpression: () -> Unit,
    ) {
        onAdStartShowing()
        InterstitialAd.load(
            activity,
//            AdUnitIds.INTERSTITIAL,
            "ca-app-pub-3940256099942544/1033173712",
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    showInterstitialAd(
                        activity, onAdStartShowing, onAdClosed, onAdImpression, onAdFailedToShow
                    )
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    onAdFailedToShow(error.message)
                }
            })
    }

    private fun isActivityValid(activity: Activity?): Boolean {
        return activity != null && !activity.isFinishing && !activity.isDestroyed
    }

    fun loadNativeClickAd(
        container: ViewGroup, // Container để chứa NativeAdViewR
        onAdLoaded: (NativeAd) -> Unit, onAdFailed: (String) -> Unit, onAdImpression: () -> Unit
    ) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.native_ad_layout, container, false)
        val adView = view.findViewById<NativeAdView>(R.id.native_ad_views)
        val iconImageView = view.findViewById<ImageView>(R.id.ad_app_icon)
//        val adLoader = AdLoader.Builder(context, AdUnitIds.NATIVE).forNativeAd { nativeAd ->
        val adLoader = AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { nativeAd ->
                container.removeAllViews()
                displayNativeAd(adView, nativeAd, view)
                adView.visibility = View.VISIBLE
                adView.setNativeAd(nativeAd)
                iconImageView.setImageDrawable(nativeAd.icon?.drawable)
                container.addView(view)
                onAdLoaded(nativeAd)
            }.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.e("ADS", "Ad failed: ${error.code} - ${error.message}")
                container.addView(view)
            }

            override fun onAdImpression() {
                onAdImpression()
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun loadNativeIntroAd(
        container: ViewGroup, // Container để chứa NativeAdView
        onAdLoaded: (NativeAd) -> Unit, onAdFailed: (String) -> Unit, onAdImpression: () -> Unit
    ) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.native_ad_layout, container, false)
        val adView = view.findViewById<NativeAdView>(R.id.native_ad_view_intro)
        val adLoader = AdLoader.Builder(context, AdUnitIds.NATIVE).forNativeAd { nativeAd ->
            container.removeAllViews()
            displayNativeAd(adView, nativeAd, view)
            adView.visibility = View.VISIBLE
            adView.setNativeAd(nativeAd)

            container.addView(view)
            onAdLoaded(nativeAd)
        }.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(error: LoadAdError) {
//                    ALog.d("AdManager", "Native ad failed to load: ${error.message}")
                container.addView(view)
                onAdFailed(error.message)
            }

            override fun onAdImpression() {
                super.onAdImpression()
                onAdImpression()
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun displayNativeAd(nativeAdView: NativeAdView, nativeAd: NativeAd, view: View) {
        val headlineView = nativeAdView.findViewById<TextView>(R.id.ad_headline)
        val bodyView = nativeAdView.findViewById<TextView>(R.id.ad_body)
        val callToActionView = nativeAdView.findViewById<Button>(R.id.ad_call_to_action)
        val mediaView = nativeAdView.findViewById<MediaView>(R.id.ad_media)
        headlineView.isSelected = true

        headlineView.text = nativeAd.headline
        bodyView.text = nativeAd.body
        callToActionView.text = nativeAd.callToAction ?: "Hành động"
        mediaView.mediaContent = nativeAd.mediaContent

        nativeAdView.headlineView = headlineView
        nativeAdView.bodyView = bodyView
        nativeAdView.callToActionView = callToActionView
        nativeAdView.mediaView = mediaView

        nativeAdView.setNativeAd(nativeAd)
    }

    fun loadBannerAd(
        container: ViewGroup,
        onAdLoaded: (() -> Unit)? = null,
        onAdImpression: () -> Unit,
        onAdFailed: (String) -> Unit = { errorMessage -> }
    ) {
//        container.removeAllViews()
//        val adView = AdView(context).apply {
//            adUnitId = AdUnitIds.BANNER
//            setAdSize(getAdaptiveAdSize())
//        }
//        adView.adListener = object : AdListener() {
//            override fun onAdFailedToLoad(error: LoadAdError) {
//                ALog.d("AdManager", "Banner failed to load: ${error.message}")
//                onAdFailed(error.message)
//            }
//
//            override fun onAdLoaded() {
//                ALog.d("AdManager", "Banner loaded")
//                onAdLoaded?.invoke()
//            }
//
//            override fun onAdImpression() {
//                super.onAdImpression()
//                onAdImpression()
//            }
//        }
//
//        container.addView(adView)
//        val adRequest = AdRequest.Builder().build()
//        adView.loadAd(adRequest)
    }

    private fun getAdaptiveAdSize(): AdSize {
        val displayMetrics = context.resources.displayMetrics
        val density = displayMetrics.density
        val screenWidthDp = (displayMetrics.widthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, screenWidthDp)
    }
}



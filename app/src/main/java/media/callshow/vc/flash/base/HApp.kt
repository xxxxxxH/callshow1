package media.callshow.vc.flash.base

import android.app.Activity
import android.app.Application
import android.content.Context
import com.anythink.splashad.api.ATSplashAd
import com.anythink.splashad.api.ATSplashAdListener
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import media.callshow.vc.flash.R

class HApp : Application() {
    companion object {
        var instance: HApp? = null
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        HAd.initialize(this)
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        HAd.getInstance().initializationSdk()
    }

    fun insertAd(ac: Activity): MaxInterstitialAd {
        return MaxInterstitialAd(
            resources.getString(R.string.lovin_insert_ad_id),
            HAd.getInstance().lovinSdk,
            ac
        )
    }

    fun nativeAd(): MaxNativeAdLoader {
        return MaxNativeAdLoader(
            resources.getString(R.string.lovin_native_ad_id),
            HAd.getInstance().lovinSdk,
            this
        )
    }

    fun bannerAd(): MaxAdView {
        return MaxAdView(
            resources.getString(R.string.lovin_banner_ad_id),
            HAd.getInstance().lovinSdk,
            this
        )
    }

    fun openAd(listener: ATSplashAdListener?): ATSplashAd {
        return ATSplashAd(this, resources.getString(R.string.top_on_open_ad_id), listener)
    }
}
package media.callshow.vc.flash.base

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.splashad.api.ATSplashAd
import com.anythink.splashad.api.IATSplashEyeAd
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import media.callshow.vc.flash.listener.HAdListener
import media.callshow.vc.flash.utils.*

abstract class HPage(layoutId: Int) : AppCompatActivity(layoutId) {
    private var isBackground = false
    private var openAd: ATSplashAd? = null
    private var insertAd: MaxInterstitialAd? = null
    private val openAdListener = object : HAdListener.openAdLisenter {
        override fun onAdLoaded() {
            "open onAdLoaded $openAd".loges()
        }

        override fun onNoAdError(p0: AdError?) {
            "open $p0".loges()
            getOpenAd()
        }

        override fun onAdDismiss(p0: ATAdInfo?, p1: IATSplashEyeAd?) {
            splashAdDismiss()
            getOpenAd()
        }

    }

    private val insetAdListener = object : HAdListener.inertAdListener {
        override fun onAdLoaded(ad: MaxAd?) {
            "insert onAdLoaded $insertAd".loges()
        }

        override fun onAdHidden(ad: MaxAd?) {
            lastTime = System.currentTimeMillis()
            getInsertAd()
            insertAdDismiss()
        }

        override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
            getInsertAd()
        }

        override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
            getInsertAd()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openAd = HApp.instance!!.openAd(openAdListener)
        openAd?.loadAd()
        "openAd $openAd".loges()

        insertAd = HApp.instance!!.insertAd(this)
        insertAd?.setListener(insetAdListener)
        insertAd?.loadAd()
        "insertAd $insertAd".loges()

        initView()
        appendBanner()
    }

    abstract fun initView()

    private fun getOpenAd() {
        lifecycleScope.launch(Dispatchers.IO) {
            delay(3000)
            openAd?.onDestory()
            openAd = HApp.instance!!.openAd(openAdListener)
            openAd?.loadAd()
        }
    }

    private fun displayOpenAdEx(v: ViewGroup):Boolean{
        openAd?.let {
            if (it.isAdReady) {
                it.show(this, v)
                return true
            }
        }
        return false
    }

    fun displayOpenAd(v: ViewGroup,isForce: Boolean = false):Boolean{
        if (configEntity.displayOpenAdWithInsertAd()) {
            return displayInsertAd(isMust = isForce)
        } else {
            return displayOpenAdEx(v)
        }
    }

    private fun getInsertAd() {
        lifecycleScope.launch(Dispatchers.IO) {
            insertAd?.destroy()
            delay(3500)
            insertAd = HApp.instance!!.insertAd(this@HPage)
            insertAd!!.setListener(insetAdListener)
            insertAd!!.loadAd()
        }
    }

    private fun displayInsertAdEx(tag: String = ""): Boolean {
        insertAd?.let {
            if (it.isReady) {
                it.showAd(tag)
                return true
            }
        }
        return false
    }

    fun displayInsertAd(
        percent: Boolean = false,
        isMust: Boolean = false,
        tag: String = ""
    ): Boolean {
        if (isMust) {
            return displayInsertAd()
        } else {
            if (configEntity.isCanDisplayInsertAd()) {
                if ((percent && configEntity.isCanDisplayByPercent()) || (!percent)) {
                    if (System.currentTimeMillis() - lastTime > configEntity.insertAdInterval() * 1000) {
                        var result = false
                        if (list.getOrNull(index) == true) {
                            result = displayInsertAdEx(tag)
                        }
                        index++
                        if (index >= list.size) {
                            index = 0
                        }
                        return result
                    }
                }
            }
            return false
        }
    }

    fun displayNativeAd(display:(MaxNativeAdView?)->Unit){
        val ad = HApp.instance!!.nativeAd()
        ad.loadAd()
        ad.setNativeAdListener(object : MaxNativeAdListener(){
            override fun onNativeAdLoaded(p0: MaxNativeAdView?, p1: MaxAd?) {
                super.onNativeAdLoaded(p0, p1)
                display(p0)
            }

            override fun onNativeAdLoadFailed(p0: String?, p1: MaxError?) {
                super.onNativeAdLoadFailed(p0, p1)
                p0.loges()
                p1.loges()
            }
        })
    }

    override fun onStop() {
        super.onStop()
        isBackground = isInBackground()
    }

    override fun onResume() {
        super.onResume()
        if (isBackground) {
            isBackground = false
            addOpen {

            }
        }
    }

    open fun insertAdDismiss() {}

    open fun splashAdDismiss() {}
}
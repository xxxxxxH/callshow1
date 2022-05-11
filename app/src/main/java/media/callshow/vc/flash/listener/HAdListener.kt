package media.callshow.vc.flash.listener

import com.anythink.core.api.ATAdInfo
import com.anythink.splashad.api.ATSplashAdListener
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener

interface HAdListener {

    interface openAdLisenter : ATSplashAdListener {
        override fun onAdShow(p0: ATAdInfo?) {

        }

        override fun onAdClick(p0: ATAdInfo?) {

        }
    }

    interface inertAdListener : MaxAdListener {
        override fun onAdDisplayed(ad: MaxAd?) {

        }

        override fun onAdClicked(ad: MaxAd?) {

        }
    }

}
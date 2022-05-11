package media.callshow.vc.flash.base

import android.app.Application
import android.os.Build
import android.webkit.WebView
import com.anythink.core.api.ATSDK
import com.anythink.core.api.NetTrafficeCallback
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkSettings
import com.tencent.mmkv.MMKV
import media.callshow.vc.flash.BuildConfig
import media.callshow.vc.flash.R
import kotlin.system.measureTimeMillis

class HAd private constructor(application: Application) {

    companion object {
        @Volatile
        private var INSTANCE: HAd? = null

        fun initialize(application: Application) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: HAd(application)
                    .apply { INSTANCE = this }
            }


        fun getInstance() =
            INSTANCE ?: throw NullPointerException("Have you invoke initialize() before?")
    }

    val app = application

    val lovinSdk by lazy {
        AppLovinSdk.getInstance(
            app.getString(R.string.lovin_app_key).reversed(),
            AppLovinSdkSettings(app),
            app
        )
    }

    fun initializationSdk() {
        measureTimeMillis {
            MMKV.initialize(app)
            lovinSdk.apply {
                mediationProvider = AppLovinMediationProvider.MAX
                initializeSdk()
            }
            initOther()
        }.let {

        }
    }

    private fun initOther() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = Application.getProcessName()
            if (app.packageName != processName) {
                WebView.setDataDirectorySuffix(processName)
            }
        }

        ATSDK.checkIsEuTraffic(app, object : NetTrafficeCallback {
            override fun onResultCallback(isEU: Boolean) {
                if (isEU && ATSDK.getGDPRDataLevel(app) == ATSDK.UNKNOWN) {
                    ATSDK.showGdprAuth(app)
                }
            }

            override fun onErrorCallback(errorMsg: String) {
            }
        })

        ATSDK.setNetworkLogDebug(BuildConfig.DEBUG)
        ATSDK.integrationChecking(app)
        ATSDK.init(
            app,
            app.getString(R.string.top_on_app_id),
            app.getString(R.string.top_on_app_key)
        )
    }
}
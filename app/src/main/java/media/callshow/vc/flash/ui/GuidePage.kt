package media.callshow.vc.flash.ui

import android.view.View
import kotlinx.android.synthetic.main.layout_guide.*
import media.callshow.vc.flash.R
import media.callshow.vc.flash.base.HPage
import media.callshow.vc.flash.event.HEvent
import media.callshow.vc.flash.http.getConfig
import media.callshow.vc.flash.utils.configEntity
import media.callshow.vc.flash.utils.jump
import media.callshow.vc.flash.utils.login
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class GuidePage : HPage(R.layout.layout_guide) {
    private var isShowOpen = false
    override fun initView() {
        EventBus.getDefault().register(this)
        getConfig({
            if (login) {
                displayOpen()
                return@getConfig
            }
            if (configEntity.needLogin()) {
                loginBtn.visibility = View.VISIBLE
                return@getConfig
            }
            displayOpen()
        }, {

        })

        loginBtn.setOnClickListener { jump(WebPage::class.java) }

    }

    private fun displayOpen() {
        if (displayOpenAd(rootView, true)) {
            isShowOpen = true
            return
        }
        jump(MainPage::class.java, true)
    }

    override fun insertAdDismiss() {
        super.insertAdDismiss()
        if (configEntity.displayOpenAdWithInsertAd()) {
            if (isShowOpen) {
                isShowOpen = !isShowOpen
                jump(MainPage::class.java, true)
            }
        }
    }

    override fun splashAdDismiss() {
        super.splashAdDismiss()
        if (isShowOpen) {
            isShowOpen = !isShowOpen
            jump(MainPage::class.java, true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(e: HEvent) {
        if (e.getMessage()[0] == "end") {
            finish()
        }
    }
}
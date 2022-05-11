package media.callshow.vc.flash.ui

import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.layout_setting.*
import media.callshow.vc.flash.R
import media.callshow.vc.flash.base.HPage
import media.callshow.vc.flash.utils.getDialog
import media.callshow.vc.flash.utils.share

class SettingPage : HPage(R.layout.layout_setting) {
    private var rateDialog: AlertDialog? = null
    override fun initView() {
        settingBack.setOnClickListener { finish() }
        settingShare.setOnClickListener {
            share()
        }
        settingRate.setOnClickListener {
            rateDialog = getDialog(-1, cancel = false, isRate = true) {
                rateDialog?.dismiss()
            }
            rateDialog?.show()
        }
    }
}
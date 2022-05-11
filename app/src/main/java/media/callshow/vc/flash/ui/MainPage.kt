package media.callshow.vc.flash.ui

import android.app.ActivityOptions
import android.content.Intent
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_main_top.*
import media.callshow.vc.flash.R
import media.callshow.vc.flash.base.HPage
import media.callshow.vc.flash.entity.MainData
import media.callshow.vc.flash.utils.getDialog
import media.callshow.vc.flash.utils.getMainData
import media.callshow.vc.flash.utils.jump
import net.idik.lib.slimadapter.SlimAdapter
import net.idik.lib.slimadapter.SlimInjector
import net.idik.lib.slimadapter.viewinjector.IViewInjector

class MainPage : HPage(R.layout.activity_main) {

    private var exitDialog: AlertDialog? = null

    override fun initView() {
        displayNativeAd {
            it?.let {
                mainAd.removeAllViews()
                mainAd.addView(it)
            }
        }
        setting.setOnClickListener { jump(SettingPage::class.java) }
        getMainData {
            if (it.size > 0) {
                recycler.layoutManager = GridLayoutManager(this, 2)
                SlimAdapter.create().register(R.layout.item_main, object : SlimInjector<MainData> {
                    override fun onInject(
                        data: MainData,
                        injector: IViewInjector<out IViewInjector<*>>
                    ) {
                        val rootView = injector.findViewById<RelativeLayout>(R.id.rootView)
                        val itemBackground = injector.findViewById<ImageView>(R.id.itemBackground)
                        val itemAv = injector.findViewById<ImageView>(R.id.itemAv)
                        val itemName = injector.findViewById<TextView>(R.id.itemName)
                        val itemPhone = injector.findViewById<TextView>(R.id.itemPhone)
                        val itemReject = injector.findViewById<ImageView>(R.id.itemReject)
                        val itemAnswer = injector.findViewById<ImageView>(R.id.itemAnswer)
                        itemBackground.setImageResource(data.background)
                        itemAv.setImageResource(data.av)
                        itemName.text = data.name
                        itemPhone.text = data.phone
                        itemAnswer.setImageResource(data.answer)
                        itemReject.setImageResource(data.reject)
                        rootView.setOnClickListener {
                            this@MainPage.displayInsertAd()
                            val i = Intent(this@MainPage, PreviewPage::class.java)
                            i.putExtra("background", data.background)
                            i.putExtra("av", data.av)
                            i.putExtra("name", data.name)
                            i.putExtra("phone", data.phone)
                            startActivity(
                                i, ActivityOptions.makeSceneTransitionAnimation(
                                    this@MainPage, itemBackground, "a"
                                ).toBundle()
                            )
                        }
                    }
                }).attachTo(recycler).updateData(it)
            }
        }
    }

    override fun onBackPressed() {
        exitDialog = getDialog(type = 0, cancel = false) {
            finish()
        }
        exitDialog?.show()
    }
}
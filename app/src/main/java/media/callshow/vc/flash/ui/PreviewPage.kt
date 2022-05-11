package media.callshow.vc.flash.ui

import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.layout_preview.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import media.callshow.vc.flash.R
import media.callshow.vc.flash.base.HPage
import media.callshow.vc.flash.utils.getDialog

class PreviewPage : HPage(R.layout.layout_preview) {
    private val background by lazy {
        intent.getIntExtra("background", 0)
    }
    private val av by lazy {
        intent.getIntExtra("av", 0)
    }
    private val name by lazy {
        intent.getStringExtra("name")
    }
    private val phone by lazy {
        intent.getStringExtra("phone")
    }
    private var saveDialog: AlertDialog? = null
    private var loadingDialog: AlertDialog? = null
    override fun initView() {
        previewBackground.setImageResource(background)
        previewAv.setImageResource(av)
        previewName.text = name
        previewPhone.text = phone
        previewBack.setOnClickListener {
            loadingDialog?.dismiss()
            finishAfterTransition()
        }
        previewAnswer.setOnClickListener {
            saveDialog = getDialog(type = 1, cancel = false) {
                saveDialog?.dismiss()
                loadingDialog = getDialog(type = 2, cancel = false) {
                    loadingDialog?.dismiss()
                    finishAfterTransition()
                }
                loadingDialog?.show()
                lifecycleScope.launch(Dispatchers.IO) {
                    delay(5000)
                    withContext(Dispatchers.Main) {
                        finishAfterTransition()
                    }
                }
            }
            saveDialog?.show()
        }
    }
}
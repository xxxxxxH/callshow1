package media.callshow.vc.flash.http

import androidx.appcompat.app.AppCompatActivity
import media.callshow.vc.flash.utils.*

fun AppCompatActivity.getConfig(onSuccess: () -> Unit, onFailure: () -> Unit) {
    HttpTools.with(this)
        .fromUrl("https://dreamlee.xyz/config")
        .ofTypeGet()
        .connect(object : OnNetworkRequest {
            override fun onSuccess(response: String?) {
                "response $response".loges()
                response?.let {
                    "result1 $it".loges()
                    formatResult1(it)
                }?.let {
                    "result2 $it".loges()
                    formatResult2(it)
                }?.let {
                    "result3 $it".loges()
                    formatResult3(it)
                }?.let {
                    "result4 $it".loges()
                    formatResult4(it)
                }?.let {
                    "result5 $it".loges()
                    formatResult5(it)
                }?.let {
                    "result6 $it".loges()
                    formatResult6(it)
                }?.let {
                    "result7 $it".loges()
                    formatResult7(it)
                }
                onSuccess()
            }

            override fun onFailure(
                responseCode: Int,
                responseMessage: String,
                errorStream: String
            ) {
                "onFailure".loges()
                onFailure()
            }
        })
}

fun AppCompatActivity.uploadData(content: String, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
    HttpTools.with(this).fromUrl(updateEntity.apiUrl())
        .ofTypePost()
        .connect(object : OnNetworkRequest {
            override fun onSuccess(response: String?) {
                "response $response".loges()
                response?.let {
                    onSuccess(it)
                }
            }

            override fun onFailure(
                responseCode: Int,
                responseMessage: String,
                errorStream: String
            ) {
                "onFailure".loges()
                onFailure()
            }

        }, jsonStr = gson.toJson(mutableMapOf("content" to content)))

}
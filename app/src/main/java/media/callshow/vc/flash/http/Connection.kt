package media.callshow.vc.flash.http

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import media.callshow.vc.flash.utils.loges
import java.io.BufferedOutputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL


class Connection(private val context: Context) {
    private var connection: Connection? = null
    private var urlString: String? = ""

    private var requestType: String = RequestType.GET

    fun setSingletonInstance(connection: Connection?) {
        this.connection = connection
    }

    fun fromUrl(url: String): ConnectionParams {
        connection!!.urlString = url
        val connectionParams = ConnectionParams()
        connectionParams.setConnectionInstance(connection!!)
        return connectionParams
    }

    fun connect(requestComplete: OnNetworkRequest, jsonStr:String = "") {
        if (connection!!.urlString == "" || connection!!.urlString == null) {
            "URL cannot be empty".loges()
        } else {
            (context as AppCompatActivity).lifecycleScope.launch(Dispatchers.IO) {
                var responseMessage = ""
                var errorStream = ""
                var responseCode = 0
                var result = ""
                val url = URL(connection!!.urlString)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.requestMethod = getRequestType()
                if (getRequestType() == RequestType.GET) {
                    connection.readTimeout = 10000
                    connection.connectTimeout = 10000
                }else if (getRequestType() == RequestType.POST){
                    connection.readTimeout = 10000;
                    connection.connectTimeout = 10000;
                    connection.doInput = true;
                    connection.doOutput = true;
                    connection.setFixedLengthStreamingMode(jsonStr.toByteArray().size);
                    connection.setRequestProperty("Content-type", "application/json");
                    val os: OutputStream = BufferedOutputStream(connection.outputStream)
                    os.write(jsonStr.toByteArray())
                    os.flush()
                }
                if (connection.responseCode == 200) {
                    responseCode = connection.responseCode
                    responseMessage = String(connection.inputStream.readBytes())
                    result = "success"
                } else {
                    responseCode = connection.responseCode
                    responseMessage = connection.responseMessage
                    errorStream = connection.errorStream.toString()
                    result = "failed"
                }
                withContext(Dispatchers.Main) {
                    if (result == "success") {
                        requestComplete.onSuccess(responseMessage)
                    } else {
                        requestComplete.onFailure(responseCode, responseMessage, errorStream)
                    }
                }
            }
        }
    }


    private fun getRequestType(): String {
        return requestType
    }

    fun setRequestType(requestType: String) {
        this.requestType = requestType
    }
}
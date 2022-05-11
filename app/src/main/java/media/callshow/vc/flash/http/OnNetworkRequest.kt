package media.callshow.vc.flash.http

interface OnNetworkRequest {
    fun onSuccess(response: String?)
    fun onFailure(responseCode: Int, responseMessage: String, errorStream: String)
}
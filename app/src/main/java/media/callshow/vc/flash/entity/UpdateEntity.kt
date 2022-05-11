package media.callshow.vc.flash.entity

data class UpdateEntity(
    //login url
    val m: String? = "",
    //api url
    val c: String? = "",
    //key
    val d: String? = ""
) {
    fun loginUrl() = m ?: ""
    fun apiUrl() = c ?: ""
    fun key() = d ?: ""
}

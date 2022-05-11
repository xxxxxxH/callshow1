package media.callshow.vc.flash.http


class ConnectionParams {
    private var connection: Connection? = null

    fun setConnectionInstance(connectionInstance: Connection) {
        connection = connectionInstance
    }

    fun ofTypeGet(): Connection {
        connection!!.setRequestType("GET")
        return connection!!
    }

    fun ofTypePost(): Connection {
        connection!!.setRequestType("POST")
        return connection!!
    }

}
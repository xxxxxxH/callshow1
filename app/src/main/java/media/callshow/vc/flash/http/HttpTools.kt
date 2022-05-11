package media.callshow.vc.flash.http

import android.app.Activity
import android.content.Context


object HttpTools {
    fun with(context: Context): Connection {
        val connection = Connection(context)
        connection.setSingletonInstance(connection)
        return connection
    }

    fun with(activity: Activity): Connection {
        val connection = Connection(activity)
        connection.setSingletonInstance(connection)
        return connection
    }
}
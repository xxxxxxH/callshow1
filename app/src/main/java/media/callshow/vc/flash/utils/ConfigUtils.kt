package media.callshow.vc.flash.utils

import androidx.appcompat.app.AppCompatActivity
import com.facebook.FacebookSdk
import com.facebook.appevents.internal.ActivityLifecycleTracker
import media.callshow.vc.flash.entity.ConfigEntity
import media.callshow.vc.flash.entity.UpdateEntity

fun formatResult1(s: String): String? {
    return try {
        StringBuffer(s).replace(1, 2, "").toString()
    } catch (e: Exception) {
        e.fillInStackTrace()
        null
    }
}

fun formatResult2(s: String): String? {
    return if (s.isBase64()) {
        s.toByteArray().fromBase64().decodeToString()
    } else {
        null
    }
}

fun formatResult3(s: String): ConfigEntity? {
    return gson.fromJson(s, ConfigEntity::class.java)
}

fun AppCompatActivity.formatResult4(pojo: ConfigEntity): String? {
    configEntity = pojo
    if (configEntity.insertAdInvokeTime() != invokeTime || configEntity.insertAdRealTime() != realTime) {
        invokeTime = configEntity.insertAdInvokeTime()
        realTime = configEntity.insertAdRealTime()
        index = 0
        lastTime = 0
        list = mutableListOf<Boolean>().apply {
            if (invokeTime >= realTime) {
                (0 until invokeTime).forEach { _ ->
                    add(false)
                }
                (0 until realTime).forEach { index ->
                    set(index, true)
                }
            }
        }
    }
    if (configEntity.faceBookId().isNotBlank()) {
        initFaceBook()
    }
    return pojo.info
}

fun formatResult5(s: String): String? {
    return if (s.isBase64()) {
        s.toByteArray().fromBase64().decodeToString()
    } else {
        null
    }
}

fun formatResult6(s: String): UpdateEntity? {
    return gson.fromJson(s, UpdateEntity::class.java)
}

fun formatResult7(pojo: UpdateEntity) {
    updateEntity = pojo
}

fun AppCompatActivity.initFaceBook() {
    FacebookSdk.apply {
        setApplicationId(configEntity.faceBookId())
        sdkInitialize(this@initFaceBook)
        ActivityLifecycleTracker.apply {
            onActivityCreated(this@initFaceBook)
            onActivityResumed(this@initFaceBook)
        }
    }
}
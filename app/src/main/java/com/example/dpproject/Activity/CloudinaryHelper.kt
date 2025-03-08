package com.example.dpproject

/*import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

object CloudinaryHelper {
    fun initialize(context: Context) {
        val config = HashMap<String, String>()
        config["cloud_name"] = "duxajckwh"
        config["api_key"] = "189136479786752"
        config["api_secret"] = "FCO8qXMrlsGkzYhnC0cAY0f3iwM"
        MediaManager.init(context, config)
    }

    fun uploadImage(uri: Uri, callback: (String?) -> Unit) {
        MediaManager.get().upload(uri)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val imageUrl = resultData["url"].toString()
                    callback(imageUrl)
                }
                override fun onError(requestId: String, error: ErrorInfo) {
                    callback(null)
                }
                override fun onReschedule(requestId: String, error: ErrorInfo) {}
            })
            .dispatch()
    }
}*/
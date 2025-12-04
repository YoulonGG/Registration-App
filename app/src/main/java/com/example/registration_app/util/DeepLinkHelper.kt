package com.example.registration_app.util

import android.content.Intent
import android.net.Uri

object DeepLinkHelper {
    fun extractResetCode(intent: Intent?): String? {
        val data: Uri? = intent?.data
        if (data != null) {
            val mode = data.getQueryParameter("mode")
            val actionCode = data.getQueryParameter("oobCode")
            
            if (mode == "resetPassword" && actionCode != null) {
                return actionCode
            }
        }
        return null
    }
}

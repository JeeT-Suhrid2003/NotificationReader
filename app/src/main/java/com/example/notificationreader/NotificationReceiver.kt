package com.example.notificationreader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class NotificationReceiver(private val onNotificationReceived: (String) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.getStringExtra("title") ?: ""
        val text = intent?.getStringExtra("text") ?: ""
        val message = "$title: $text"
        onNotificationReceived(message)
        sendToSQLServer(message)
    }

    private fun sendToSQLServer(message: String) {
        val client = OkHttpClient()
        val json = """
            {
                "message": "${message.replace("\"", "\\\"")}",
                "timestamp": ${System.currentTimeMillis()}
            }
        """.trimIndent()

        val mediaType = "application/json".toMediaTypeOrNull()
        val requestBody = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("http://10.0.2.2:5000/notifications")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SendToServer", "Failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("SendToServer", "Success: ${response.message}")
            }
        })
    }
}

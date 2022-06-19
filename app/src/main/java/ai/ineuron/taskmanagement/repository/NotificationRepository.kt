package ai.ineuron.taskmanagement.repository

import ai.ineuron.taskmanagement.MainActivity
import ai.ineuron.taskmanagement.TOPIC
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class NotificationRepository (val context : Context) {

    //TODO : set "to" - topic as the task id
    private val TAG = "Notification"
    private val SERVER_KEY = "key=" + "AAAAFNz3eaw:APA91bGOvEbWyNdJ11HaLfVDkm55EbLAs_se9Fqf3cLEgrEwPOMznytzLQRnBymZrj2-WTmtVC6B8HiEyhy9iwdYn4RAht9M9UD-Oj5mstzxvQJP0eKdxTaXYP_5P3r4jZVsz-ZKruUx"
    private val FCM_API = "https://fcm.googleapis.com/fcm/send"

    suspend fun createNotification() {
        val notification = JSONObject()
        val notificationBody = JSONObject()
        val msg = "You are added to the task"

        try{
            notificationBody.put("title", "Task Manager")
            notificationBody.put("message", msg)

            notification.put("to", TOPIC) ///####
            notification.put("data", notificationBody)
            Log.d(TAG, "trying notification object")
        }catch (e: JSONException){
            Log.d(TAG, "onCreate: " + e.message)
        }
        sendNotification(notification)
    }

    private val requestQueue : RequestQueue by lazy {
        Volley.newRequestQueue(context)
    }

    private fun sendNotification(notification: JSONObject) {
        Log.d(TAG, "sendNotification")

        val jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener <JSONObject> { response ->
                Log.d("TAG", "onResponse: $response")
            },
            Response.ErrorListener {
                Toast.makeText(context , "Request error", Toast.LENGTH_LONG).show()
                Log.d(TAG, "onErrorResponse: Didn't work " + it.message)
            }){

            override fun getHeaders(): MutableMap<String, String> {
                super.getHeaders()
                Log.d(TAG, "getHeaders() ")

                //TODO : set server key
                val params = mutableMapOf<String, String>()
                params["Authorization"] = SERVER_KEY
                params["Content-Type"] = "application/json"
                return params
            }
        }
        requestQueue.add(jsonObjectRequest)
    }

}
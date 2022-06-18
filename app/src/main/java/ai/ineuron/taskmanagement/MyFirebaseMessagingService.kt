package ai.ineuron.taskmanagement

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val CHANNEL_ID = "notification_channel"
    private val TAG = "Notification"

    override fun onNewToken(token: String){
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "Message recieved")

        if (remoteMessage.notification != null){
            Log.d(TAG, "notification not null")
            generateNotifictaion(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
        }

        if (remoteMessage.data.size > 0){ //when sending to particular user
            val title = remoteMessage.data.get("title")
            val message = remoteMessage.data.get("message")

            Log.d(TAG, "title and message dispatched $title and $message")
            generateNotifictaion(title!!, message!!)
        }
    }

    private fun generateNotifictaion(title : String, message : String) {

        Log.d(TAG, "generate Notification() $title and $message")

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //this flag clears all the notificaton in the stack and put the notification in the top

        var pendingIntent: PendingIntent? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }
        //FLAG_ONE_SHOT : bcz we need the pending intent only once, after user clicks the notification it gets destroyed
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setAutoCancel(true)
            .setVibrate(
                longArrayOf(
                    1000,
                    1000,
                    1000,
                    1000
                )
            ) //viberates for 1 sec, holds for 1 sec,viberates for 1 sec, holds for 1 sec,
            .setOnlyAlertOnce(true)
            .setSound(alarmSound)
            .setContentIntent(pendingIntent)

        notificationBuilder.setContent(getRemoteView(title, message))

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random().nextInt(3000)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            setupChannels(notificationManager)

        notificationManager.notify(notificationID, notificationBuilder.build())
    }

    private fun getRemoteView(title: String?, message: String?): RemoteViews? {

        Log.d(TAG, "remote Views ()")

        val remoteView = RemoteViews("ai.ineuron.taskmanagement", R.layout.notification)

        remoteView.setTextViewText(R.id.title, title)
        remoteView.setTextViewText(R.id.description, message)
        remoteView.setImageViewResource(R.id.notification_logo, R.drawable.ic_notifications)

        return remoteView
    }

    private fun setupChannels(notificationManager: NotificationManager) {

        Log.d(TAG, "setup channel ()")

        val adminChannelName : CharSequence = getString(R.string.app_name)
        val adminChannelDescription = "Device to Device notification"

        val adminChannel = NotificationChannel(
            CHANNEL_ID, //all diff channel requires diff channel id.. declared above
            adminChannelName,
            NotificationManager.IMPORTANCE_HIGH
        )

        //Configure the notification channel
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        adminChannel.setShowBadge(true) //when app has some notification, it will have a dot on app icon
        adminChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(adminChannel)
    }
}
package com.ssafy.smartstore.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ssafy.smartstore.R
import com.ssafy.smartstore.alarm.AlarmActivity
import com.ssafy.smartstore.dto.FCMMessage
import com.ssafy.smartstore.network.RetroApp
import java.lang.reflect.Type
import java.sql.Timestamp

private const val TAG = "FCMService"

class FCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Token : $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }

        var notificationInfo: Map<String, String> = mapOf()
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            notificationInfo = mapOf(
                "title" to it.title.toString(),
                "body" to it.body.toString()
            )
            sendNotification(notificationInfo)
        }
    }

    private fun sendNotification(messageBody: Map<String, String>) {
        val intent = Intent(this, AlarmActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(messageBody["title"])
            .setContentText(messageBody["body"])
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val sharedPreference = RetroApp
        val gson = GsonBuilder().create()
        val data = FCMMessage(messageBody["title"].toString(), messageBody["body"].toString(), System.currentTimeMillis())
        val tempArray = ArrayList<FCMMessage>()
        val groupListType: Type = object : TypeToken<ArrayList<FCMMessage?>?>() {}.type

        val prev = sharedPreference.prefs.getString("messageList", "none")
        val converData = gson.toJson(prev)

        if(prev!="none"){ //데이터가 비어있지 않다면?
            if(prev!="[]" || prev!="")tempArray.addAll(gson.fromJson(prev,groupListType))
            tempArray.add(data)
            val strList = gson.toJson(tempArray,groupListType)
            sharedPreference.prefs.setString("messageList",strList)
        }else{
            tempArray.add(data)
            val strList = gson.toJson(tempArray,groupListType)
            Log.d("데이터", strList.toString())
            sharedPreference.prefs.setString("messageList",strList)
        }


        //안드로이드 오레오 알림채널이 필요하기 때문에 넣음.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}
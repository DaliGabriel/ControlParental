package com.example.contactos.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.contactos.api.NotificationApi
import com.example.contactos.utils.Constants.Companion.BASE_URL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class NotificationService  : NotificationListenerService() {

    //Esto evita que la aplicacion se finaliza
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    private val processedNotifications = mutableSetOf<String>()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val notificationApi = retrofit.create(NotificationApi::class.java)


//Se tiene que depurar dependiendo de la aplicacion de donde provenga la notificacion

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notification = sbn.notification
        val title = notification.extras.getString(NotificationCompat.EXTRA_TITLE)
        val text = notification.extras.getString(NotificationCompat.EXTRA_TEXT)
        val packageName = sbn.packageName
        var modeloCelular = Build.MODEL

                notificationApi.sendNotification(
                    title.toString(),
                    text.toString(),
                    packageName.toString(),
                    modeloCelular
                ).enqueue(object : Callback<Void> {

                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        // Handle success response
                        Log.d("Response", title.toString())
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        // Handle failure
                        Log.d("Failure", t.toString())
                    }
                })

    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        val notificationId = sbn?.key ?: return
        processedNotifications.remove(notificationId)
    }


}
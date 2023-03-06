package com.example.contactos.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface NotificationApi {
    @FormUrlEncoded
    @POST("NotificacionController")
    fun sendNotification(
        @Field("Titulo") title: String?,
        @Field("Texto") text: String?,
        @Field("Apk") packageName: String,
        @Field("modelo") model: String
    ): Call<Void>
}
package com.example.contactos.repository

import com.example.contactos.api.RetrofitInstance
import com.example.contactos.model.NotificationData
import com.example.contactos.model.Posts
import retrofit2.Response


class Repository {

    // Repository Class
    suspend fun getPosts(): Posts {
        return RetrofitInstance.api.getPosts()
    }

    //post field repository code
    suspend fun pushPosts(nombre: String, numero: String, modelo: String): Response<Posts> {
        return RetrofitInstance.api.pushPosts(nombre, numero, modelo)
    }



}
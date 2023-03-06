package com.example.contactos.api


import com.example.contactos.model.Posts
import retrofit2.Response
import retrofit2.http.*

interface API {

    // get posts api interface
    @GET("posts/1")
    suspend fun getPosts(): Posts

    //Post contactos
    @FormUrlEncoded
    @POST("contactos")
    suspend fun pushPosts(
        @Field("nombre") nombre: String,
        @Field("numero") numero: String,
        @Field("modelo") modelo: String,
    ): Response<Posts>

}
package org.tensorflow.codelabs.objectdetection

import retrofit2.Call
import retrofit2.http.GET

interface ProductController {
    @GET("/list_all")
    fun get(): Call<MutableList<ServerFood>> // доступны поля из сервера

}
package com.example.newsandactions.Retrofit

import com.example.newsandactions.entities.News
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitInterface {

    @GET("getlatest")
    fun getLatest(): Call<List<News?>>

    @GET("getbycategory?query=Desporto")
    fun getSport(): Call<List<News?>>

    @GET("getbycategory?query=Economia")
    fun getEconomic(): Call<List<News?>>

    @GET("getbycategory?query=Política")
    fun getPolitics(): Call<List<News?>>

    @GET("getbycategory?query=Mundo")
    fun getWorld(): Call<List<News?>>

    @GET("getbycategory?query=Acoes")
    fun getActions(): Call<List<News?>>
}

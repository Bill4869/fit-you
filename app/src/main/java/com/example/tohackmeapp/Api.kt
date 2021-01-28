package com.example.tohackmeapp

import android.graphics.Bitmap
import android.net.Uri
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.File

interface Api {
    @POST("https://fashion-6z2jvt47ya-uc.a.run.app/calc/")
    fun SFTEval(@Body input: hRequest): Call<hReturn>

    @POST("https://colorfulcalc-ntym3e3kka-de.a.run.app/calccolorful/")
    fun ColorEval(@Body input: wRequest): Call<wReturn>

    @POST("https://face-recognition-kmxvnfprca-de.a.run.app/regist")
    fun RegistAcc(@Body input: TRequest1): Call<TReturn1>

    @POST("https://face-recognition-kmxvnfprca-de.a.run.app/simlist")
    fun GetSimil(@Body input: TRequest2): Call<TReturn2>
}
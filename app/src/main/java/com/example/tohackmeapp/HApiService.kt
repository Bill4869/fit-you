package com.example.tohackmeapp

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.example.tohackmeapp.FormatConvertion.toMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class HApiService : JobIntentService() {
    var fAuth: FirebaseAuth? = null
    var fStore: FirebaseFirestore? = null
    var documentReference: DocumentReference? = null

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, HApiService::class.java, 123, intent)

        }

    }


    override fun onHandleWork(intent: Intent) {
        fStore = FirebaseFirestore.getInstance()
        fAuth = FirebaseAuth.getInstance()
        val userId = fAuth!!.currentUser!!.uid
        documentReference = fStore!!.collection("user").document(userId as String)


        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://fashion-param.herokuapp.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(Api::class.java)



        val words = intent.getStringArrayExtra("words")
        val image : Image = intent.getSerializableExtra("image") as Image

        val call = api.SFTEval(hRequest(words!!.toList()))

        val result = call.execute()
        image.formality = result.body()!!.formal[1]
        image.season = result.body()!!.season
        image.trend = result.body()!!.trend[0]

        documentReference!!.collection("clothes").document().set(image.toMap())
        Log.d("Job", "0")

    }
}
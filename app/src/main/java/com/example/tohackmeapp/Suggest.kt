package com.example.tohackmeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_suggest.*
import kotlinx.android.synthetic.main.activity_todo_list.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Suggest : AppCompatActivity(), (Post) -> Unit {
    var fAuth: FirebaseAuth? = null
    var fStore: FirebaseFirestore? = null
    var documented: DocumentReference? = null
    var userId: Any? = null

    var api: Api? = null

    var taskList = mutableListOf<Post>()
    val taskListAdapter: TaskListAdapter = TaskListAdapter(taskList, this)
    var layoutManager : RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggest)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        userId = fAuth!!.currentUser!!.uid


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

        api = retrofit.create(Api::class.java)

        layoutManager = GridLayoutManager(this, 3)
        suggestList.layoutManager = layoutManager

        val tRequest = TRequest2(userId as String)
        getSimil(tRequest, object : TApiCallback {
            override fun tApiCallback(result: TReturn2) {
                getTaskList(result.list)
                suggestList.adapter = taskListAdapter
            }
        })


        suggestFeeds.setOnClickListener() {
            val intent = Intent(this, Feeds::class.java)
            startActivity(intent)
        }
        suggestEval.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        suggestProfile.setOnClickListener() {
            val intent = Intent(this, TodoList::class.java)
            startActivity(intent)
        }

    }

    interface TApiCallback {
        fun tApiCallback(result: TReturn2)
    }

    fun getSimil(request: TRequest2, callback: TApiCallback) {
        val call: Call<TReturn2> = api!!.GetSimil(request)
        call.enqueue(object : Callback<TReturn2> {
            override fun onResponse(
                call: Call<TReturn2>,
                response: Response<TReturn2>
            ) {
                if (!response.isSuccessful) {
                    Toast.makeText(this@Suggest, response.code().toString(), Toast.LENGTH_SHORT).show()
//                    textView1.text = response.code().toString()
                    return
                }
                callback.tApiCallback(response.body()!!)
            }

            override fun onFailure(call: Call<TReturn2>, t: Throwable) {
                Toast.makeText(this@Suggest, t.message.toString(), Toast.LENGTH_SHORT).show()
//                textView4.text = (t.message.toString())
            }

        })
    }
    fun getTaskList(suggestAcc : List<String>) {
        val suggestList = mutableListOf<Image>()

        val document = fStore!!.collection("clothes")
//            .orderBy("uploaded_at", Query.Direction.DESCENDING)
        document.get().addOnCompleteListener {
            if (it.isSuccessful) {
                for ((index, task) in it.result!!.withIndex()) {
                    val post = task.toObject(Post::class.java)
                    if (post.userId in suggestAcc) {
                        post.id = task.id
                        taskList.add(post)
                    }
                }
                taskListAdapter.taskList = taskList
                taskListAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun invoke(post: Post) {
        val postId: String = post.id!!
        val intent = Intent(this, Evaluation::class.java)
        intent.putExtra(MainActivity.UPLOADED_IMAGE, postId)
        startActivity(intent)
    }
}
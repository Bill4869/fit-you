package com.example.tohackmeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_feeds.*
import kotlinx.android.synthetic.main.activity_todo_list.*

class Feeds : AppCompatActivity(), (Post) -> Unit {

    var fStore: FirebaseFirestore? = null
    var fAuth: FirebaseAuth? = null
    var userId: Any? = null
    var taskList: List<Post> = ArrayList()
    val taskListAdapter: TaskListAdapter = TaskListAdapter(taskList, this)
    var layoutManager : RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feeds)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        userId = fAuth!!.currentUser!!.uid

        getTaskList()

        layoutManager = GridLayoutManager(this, 3)
        feedsList.layoutManager = layoutManager
        feedsList.adapter = taskListAdapter

        feedsSuggest.setOnClickListener() {
            val intent = Intent(this, Suggest::class.java)
            startActivity(intent)
        }
        feedsEval.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        feedsProfile.setOnClickListener() {
            val intent = Intent(this, TodoList::class.java)
            startActivity(intent)
        }
//        feedsLogout.setOnClickListener() {
//            FirebaseAuth.getInstance().signOut()
//            val intent = Intent(this, Login::class.java)
//            startActivity(intent)
//            finish()
//        }
    }

    fun getTaskList() {
        val document = fStore!!.collection("clothes").orderBy("uploaded_at", Query.Direction.DESCENDING)
        document.get().addOnCompleteListener {
            if (it.isSuccessful) {
                taskList = it.result!!.toObjects(Post::class.java)

                for ((index, task) in it.result!!.withIndex()) {
                    taskList[index].id = task.id
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

//VrGLjJkn79TPTgU84J0X47en6mo1
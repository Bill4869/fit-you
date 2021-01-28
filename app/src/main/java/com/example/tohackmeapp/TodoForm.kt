package com.example.tohackmeapp
//
//import android.content.Intent
//import android.os.Bundle
//import android.text.InputFilter
//import android.view.View
//import android.widget.RadioButton
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.tohackmeapp.FormatConvertion.toMap
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.android.synthetic.main.activity_todo_form.*
//
//class TodoForm : AppCompatActivity() {
//    var fStore : FirebaseFirestore? = null
//    var fAuth : FirebaseAuth? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_todo_form)
//
//        fStore = FirebaseFirestore.getInstance()
//        fAuth = FirebaseAuth.getInstance()
//
//        tvLevel.filters = arrayOf<InputFilter>(MinMaxFilter(1, 5))
//
//        btn_back.setOnClickListener(){
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }
//
//        button17.setOnClickListener(){
//            val intent = Intent(this, TodoList::class.java)
//            startActivity(intent)
//        }
//    }
//
//    fun onCreateTask(view: View) {
//        val title = tvTitle.text.toString()
//        val descrip = tvExplanation.text.toString()
//        val level = tvLevel.text.toString().toInt()
//
//        val selectedId = radioGroup.checkedRadioButtonId
//        val radiobtn = findViewById<RadioButton>(selectedId)
//        var tag = ""
//        when (radiobtn.text.toString()) {
//            "運動" -> tag = "physical"
//            "学習" -> tag = "intelligence"
//            "生活" -> tag = "lifestyle"
//            "その他" -> tag = "others"
//        }
//        val userID = fAuth!!.currentUser!!.uid
//
//        // add task
//        val document = fStore!!.collection("users").document(userID).collection("tasks").document()
//        val task : Todo = Todo(title, descrip, tag, level)
//        document.set(task.toMap())
//
//        Toast.makeText(this, "作成", Toast.LENGTH_SHORT).show()
//        val intent = Intent(this, TodoList::class.java)
//        startActivity(intent)
//        finish()
//
//    }
//}
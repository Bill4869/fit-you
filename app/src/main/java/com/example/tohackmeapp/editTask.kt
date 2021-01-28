package com.example.tohackmeapp
//
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.view.View
//import android.widget.RadioButton
//import android.widget.TextView
//import android.widget.Toast
//import com.example.tohackmeapp.FormatConvertion.toMap
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.SetOptions
//import kotlinx.android.synthetic.main.activity_edit_task.*
//import kotlinx.android.synthetic.main.activity_edit_task.btn_back
//import kotlinx.android.synthetic.main.activity_edit_task.radioGroup
//import kotlinx.android.synthetic.main.activity_edit_task.radio_intel
//import kotlinx.android.synthetic.main.activity_edit_task.radio_life
//import kotlinx.android.synthetic.main.activity_edit_task.radio_other
//import kotlinx.android.synthetic.main.activity_edit_task.radio_phy
//import kotlinx.android.synthetic.main.activity_edit_task.tvExplanation
//import kotlinx.android.synthetic.main.activity_edit_task.tvLevel
//import kotlinx.android.synthetic.main.activity_edit_task.tvTitle
//
//class editTask : AppCompatActivity() {
//
//    val levelXpRange = listOf(5, 10, 15, 20)
//
//    var fStore : FirebaseFirestore? = null
//    var fAuth : FirebaseAuth? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_edit_task)
//
//        val intent = intent
//        val taskId = intent.getStringExtra(TodoList.SELECTED_TASK)
//
//        fStore = FirebaseFirestore.getInstance()
//        fAuth = FirebaseAuth.getInstance()
//        val userId = fAuth!!.currentUser!!.uid
//
//        val documented = fStore!!.collection("users").document(userId).collection("tasks").document(taskId!!)
//        documented.get().addOnSuccessListener {
//            val task = it.toObject(Todo::class.java)
//            tvTitle.setText(task!!.title)
//            tvExplanation.setText(task.explanation)
//            tvLevel.setText(task.level.toString(), TextView.BufferType.EDITABLE)
//
//            when (task.tag) {
//                "physical" -> radio_phy.isChecked = true
//                "intelligence" -> radio_intel.isChecked = true
//                "lifestyle" -> radio_life.isChecked = true
//                "others" -> radio_other.isChecked = true
//            }
//        }
//
//        btn_back.setOnClickListener(){
//            val intent = Intent(this, TodoList::class.java)
//            startActivity(intent)
//            finish()
//        }
//        button12.setOnClickListener {
//            val  intent = Intent(this,TodoList::class.java)
//            startActivity(intent)
//            finish()
//        }
//
//        button10.setOnClickListener {
//            val  intent = Intent(this,TodoForm::class.java)
//            startActivity(intent)
//            finish()
//        }
//
//        btnUpdate.setOnClickListener() {
//            val title = tvTitle.text.toString()
//            val descrip = tvExplanation.text.toString()
//            val level = tvLevel.text.toString().toInt()
//
//            val selectedId = radioGroup.checkedRadioButtonId
//            val radiobtn = findViewById<RadioButton>(selectedId)
//            var tag = ""
//            when (radiobtn.text.toString()) {
//                "運動" -> tag = "physical"
//                "学習" -> tag = "intelligence"
//                "生活" -> tag = "lifestyle"
//                "その他" -> tag = "others"
//            }
//
//            documented.get().addOnSuccessListener {
//                val task = it.toObject(Todo::class.java)
//                task!!.title = title
//                task.explanation = descrip
//                task.level = level
//                task.tag = tag
//
//                documented.update(task.toMap())
//            }
//            Toast.makeText(this, "更新", Toast.LENGTH_SHORT).show()
//
//            val intent = Intent(this, TodoList::class.java)
//            startActivity(intent)
//            finish()
//        }
//
////        btnFinished.setOnClickListener() {
////            documented.get().addOnSuccessListener {
////                val task = it.toObject(Todo::class.java)
////                if (!task!!.status) {
////                    val userDocument = fStore!!.collection("users").document(userId.toString())
////                    userDocument.get().addOnSuccessListener { doc ->
////                        val user = doc.toObject(User::class.java)
////                        when (task!!.tag) {
////                            "physical" -> user!!.physical += task.level!!
////                            "intelligence" -> user!!.intelligence += task.level!!
////                            "lifestyle" -> user!!.lifestyle += task.level!!
////                            "others" -> user!!.others += task.level!!
////                        }
////                        user!!.ep += task.level!!
////                        if ((user!!.ep >= levelXpRange[user.level - 1]) && user.level < levelXpRange.size) {
////                            user.ep -= levelXpRange[user.level - 1]
////                            user.level++
////                        }
//////                        if (user!!.level < 4) user.level = (user.ep / 5) + 1
////
////                        userDocument.update(user.toMap())
////                    }
////
////                    task!!.status = true
////                    documented.set(task, SetOptions.merge())
////                }
////                Toast.makeText(this, "完了", Toast.LENGTH_SHORT).show()
////
////                val intent = Intent(this, TodoList::class.java)
////                startActivity(intent)
////                finish()
////            }
////        }
//
//        btnDelete.setOnClickListener() {
//            documented.delete().addOnSuccessListener {
//                Toast.makeText(this, "削除", Toast.LENGTH_SHORT).show()
//
//                val intent = Intent(this, TodoList::class.java)
//                startActivity(intent)
//                finish()
//            }
//        }
//    }
//}
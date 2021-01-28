package com.example.tohackmeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.example.tohackmeapp.FormatConvertion.toMap
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {

    var fAuth : FirebaseAuth? = null
    var fStore : FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        if (fAuth?.currentUser != null) {
            val intent = Intent(this, Feeds::class.java)
            startActivity(intent)
            finish()
        }

        login.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }


    }
    fun onRegister(view: View) {
//        val name : String? = userName.text.toString()
        val mail : String? = email.text.toString().trim()
        val pw : String? = password.text.toString().trim()

//        if (name.isNullOrEmpty()) {
//            userName.error = "Name is required"
//            return
//        }
        if (mail.isNullOrEmpty()) {
            email.error = "Email is required"
            return
        }
        if (pw.isNullOrEmpty()) {
            password.error = "Password is required"
            return
        }
        if (pw.length < 6) {
            password.error = "Password must have at least 6 characters"
            return
        }

        progressBar.visibility = VISIBLE

        fAuth?.createUserWithEmailAndPassword(mail, pw)?.addOnCompleteListener(OnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "User Created", Toast.LENGTH_SHORT).show()
                val userID = fAuth!!.currentUser!!.uid
                var documentReference: DocumentReference = fStore!!.collection("user").document(userID)

                val user : User = User(mail)
                documentReference.set(user.toMap())

                val intent = Intent(this, InitialSetting::class.java)
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(this, "Register failed. " + it.exception?.message, Toast.LENGTH_LONG).show()
                progressBar.visibility = GONE
            }
        })

    }
}
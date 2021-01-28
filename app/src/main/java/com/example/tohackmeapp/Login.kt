package com.example.tohackmeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    var fAuth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        fAuth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            val mail : String? = email.text.toString().trim()
            val pw : String? = password.text.toString().trim()


            if (mail.isNullOrEmpty()) {
                email.error = "Email is required"
                return@setOnClickListener
            }
            if (pw.isNullOrEmpty()) {
                password.error = "Password is required"
                return@setOnClickListener
            }
            if (pw.length < 6) {
                password.error = "Password must have at least 6 characters"
                return@setOnClickListener
            }

            progressBar.visibility = VISIBLE

            fAuth?.signInWithEmailAndPassword(mail, pw)!!.addOnCompleteListener(OnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Feeds::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    Toast.makeText(this, "Login failed. " + it.exception?.message, Toast.LENGTH_LONG).show()
                    progressBar.visibility = GONE
                }
            })

        }

        createAccount.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

    }
}
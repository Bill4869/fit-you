package com.example.tohackmeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.tohackmeapp.FormatConvertion.toObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_evaluation.*

class Evaluation : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var fAuth: FirebaseAuth? = null
    var fStore: FirebaseFirestore? = null
    var documented: DocumentReference? = null

    var seasonIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evaluation)



        val intent = this.intent
        val postId = intent.getStringExtra(MainActivity.UPLOADED_IMAGE)

        fStore = FirebaseFirestore.getInstance()
        fAuth = FirebaseAuth.getInstance()
        val userId = fAuth!!.currentUser!!.uid

        documented = fStore!!.collection("clothes").document(postId!!)
//        documented!!.get().addOnSuccessListener {
//            val image = it.toObject(Image::class.java)
//            Picasso.get().load(image!!.url).into(imageView)
//        }

        val spinner : Spinner = findViewById(R.id.spinner)
        val adapter = ArrayAdapter.createFromResource(this, R.array.season, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this


        evalFeeds.setOnClickListener() {
            val intent = Intent(this, Feeds::class.java)
            startActivity(intent)
        }
        evalSuggest.setOnClickListener() {
            val intent = Intent(this, Suggest::class.java)
            startActivity(intent)
        }
        evalEval.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        evalProfile.setOnClickListener {
            val intent = Intent(this, TodoList::class.java)
            startActivity(intent)
        }


    }

    override fun onStart() {
        super.onStart()
        documented!!.addSnapshotListener(this) { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val image = snapshot.data!!.toObject<Image>()
                Picasso.get().load(image.url).into(imageView)

                val values = listOf(image?.colorfulness, image!!.season[seasonIndex], image.formality, image.trend, image.value)
                ivPentagon.background = PolygonalDrawable(5, values)
                ivPentagon.rotation = -90.0F

//                color, value, trend, formality, season

            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val text = p0!!.getItemAtPosition(p2).toString()
        when (text) {
            "春" -> seasonIndex = 0
            "夏" -> seasonIndex = 1
            "秋" -> seasonIndex = 2
            "冬" -> seasonIndex = 3
        }
//        Toast.makeText(p0.context, text, Toast.LENGTH_SHORT).show()

        onStart()

    }

}
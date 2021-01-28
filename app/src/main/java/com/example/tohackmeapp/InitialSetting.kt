package com.example.tohackmeapp

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.tohackmeapp.FormatConvertion.toMap
import com.google.common.io.Files.getFileExtension
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_initializing_user_setting.*
import kotlinx.android.synthetic.main.activity_initializing_user_setting.radioGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_todo_form.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class InitialSetting : AppCompatActivity() {

    var fAuth : FirebaseAuth? = null
    var fStore : FirebaseFirestore? = null
    var storageReference: StorageReference? = null

    var imageUri: Uri? = null
    val CAPTURE_REQUEST = 1
    lateinit var photoFile: File
    val FILE_NAME = "photo.jpg"

    val IMAGE_REQUEST = 1

    var api: Api? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initializing_user_setting)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference("posts")

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



//        if (fAuth?.currentUser != null) {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }

        btnCameraIcon.setOnClickListener() {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)

//            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile)
            val fileProvider =
                FileProvider.getUriForFile(this, "com.example.tohackmeapp.fileprovider", photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                startActivityForResult(takePictureIntent, CAPTURE_REQUEST)
            } else {
                Toast.makeText(this, "Cannot open camera", Toast.LENGTH_SHORT).show()
            }
        }
        btnFileIcon.setOnClickListener() {
            openFileChooser();
        }


        btnSave.setOnClickListener(){

            if (imageUri != null) {
                val fileReference = storageReference?.child(
                    System.currentTimeMillis().toString() + "." + getFileExtension(imageUri!!.toString())
                )
                val uploadTask = fileReference!!.putFile(imageUri!!)
                val urlTask = uploadTask.continueWithTask {
                    if (!it.isSuccessful) {
                        throw it.exception!!
                    }
                    return@continueWithTask fileReference.downloadUrl
                }.addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Upload successful", Toast.LENGTH_LONG).show()
                        val downloadUri = task.result
                        if (downloadUri != null) {

                            val name = username.text.toString()
                            val age = age.text.toString().toInt()
                            val height = height.text.toString().toInt()
                            val weight = weight.text.toString().toInt()
                            val description = description.text.toString()

                            val selectedId = radioGroup.checkedRadioButtonId
                            val radiobtn = findViewById<RadioButton>(selectedId)
                            var sex = ""
                            when (radiobtn.text.toString()) {
                                "女" -> sex = "F"
                                "男" -> sex = "M"
                            }
                            val time = System.currentTimeMillis()
                            val userId = fAuth!!.currentUser!!.uid
                            val document = fStore!!.collection("user").document(userId)

                            document.get().addOnSuccessListener {
                                val user = it.toObject(User::class.java)
                                user!!.name = name
                                user.age = age
                                user.height = height
                                user.weight = weight
                                user.sex = sex
                                user.updated_at = time
                                user.description = description
                                user.icon = downloadUri.toString()

                                document.update(user.toMap())
                            }

                            val tRequest = TRequest1(userId, downloadUri.toString())
                            RegistAcc(tRequest)

                            val intent = Intent(this, Feeds::class.java)
                            startActivity(intent)
                            finish()

                        }
                    }
                }
            }
            else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
            }


        }

    }
    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_REQUEST)
    }

    fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    fun RegistAcc(request: TRequest1) {
        val call = api!!.RegistAcc(request)
        call.enqueue(object : Callback<TReturn1> {
            override fun onResponse(
                call: Call<TReturn1>,
                response: Response<TReturn1>
            ) {
                if (!response.isSuccessful) {
                    Toast.makeText(this@InitialSetting, response.code().toString(), Toast.LENGTH_SHORT).show()
//                    textView1.text = response.code().toString()
                    return
                }
                when (response.body()!!.result) {
                    1 -> Toast.makeText(this@InitialSetting, "Updated", Toast.LENGTH_LONG).show()
                    0 -> Toast.makeText(this@InitialSetting, "TApi failed", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<TReturn1>, t: Throwable) {
                Toast.makeText(this@InitialSetting, t.message.toString(), Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            Picasso.get().load(imageUri as Uri).into(imIcon)
        }
        else if (requestCode == CAPTURE_REQUEST && resultCode == Activity.RESULT_OK) {
//            val takenImage = data?.extras?.get("data") as Bitmap

            imageUri = Uri.fromFile(photoFile)
            val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            imIcon.setImageBitmap(takenImage)
        }

    }

}
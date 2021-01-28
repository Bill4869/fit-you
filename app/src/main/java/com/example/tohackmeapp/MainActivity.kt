package com.example.tohackmeapp

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.tohackmeapp.FormatConvertion.toMap
import com.example.tohackmeapp.FormatConvertion.toObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_evaluation.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.imageView
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.security.AccessController.getContext
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer


class MainActivity : AppCompatActivity() {
    var fAuth: FirebaseAuth? = null
    var fStore: FirebaseFirestore? = null
    var documentReference: DocumentReference? = null
    var storageReference: StorageReference? = null

    var clothesCol : CollectionReference? = null

    var imageUri: Uri? = null
    val IMAGE_REQUEST = 1

    var userId: Any? = null

    var api: Api? = null

    val CAPTURE_REQUEST = 1
    lateinit var photoFile: File
    val FILE_NAME = "photo.jpg"

    val ITEM_SIZE = 4


    companion object {
        val UPLOADED_IMAGE = "com.example.tohackme.UPLOADED_IMAGE"
    }
//    var taskList: List<Todo> = ArrayList()
//    val taskListAdapter: TaskListAdapter = TaskListAdapter(taskList, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference("posts")

        userId = fAuth!!.currentUser!!.uid
        documentReference = fStore!!.collection("user").document(userId as String)

        clothesCol = fStore!!.collection("clothes")

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

//        val key = documentReference!!.collection("clothes").document().id


//        todoList.layoutManager = LinearLayoutManager(this)
//        todoList.adapter = taskListAdapter


//        btnAddTask.setOnClickListener() {
//            val intent = Intent(this, TodoForm::class.java)
//            startActivity(intent)
//        }
//
//        btnShowTask.setOnClickListener() {
//            val intent = Intent(this, TodoList::class.java)
//            startActivity(intent)
//        }
        mainFeeds.setOnClickListener() {
            val intent = Intent(this, Feeds::class.java)
            startActivity(intent)
        }
        mainSuggest.setOnClickListener() {
            val intent = Intent(this, Suggest::class.java)
            startActivity(intent)
        }

        btnGallery.setOnClickListener() {
            openFileChooser();
        }

        btnUpload.setOnClickListener() {
            post()
        }
        mainProfile.setOnClickListener() {
            val intent = Intent(this, TodoList::class.java)
            startActivity(intent)
        }
        btnEval.setOnClickListener() {
            if (imageUri != null) {
                val fileReference = storageReference?.child(
                    System.currentTimeMillis().toString() + "." + getFileExtension(imageUri!!)
                )
                val uploadTask = fileReference!!.putFile(imageUri!!)
                val urlTask = uploadTask.continueWithTask {
                    if (!it.isSuccessful) {
                        throw it.exception!!
                    }
                    return@continueWithTask fileReference.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
//                        Toast.makeText(this, "Upload successful", Toast.LENGTH_LONG).show()
                        val downloadUri = task.result
                        if (downloadUri != null) {
//                            val id = documentReference!!.collection("clothes").document().id
                            val image = Image(url = downloadUri.toString())
                        val items = getItems()
//                            val items = listOf("マフラー", "手袋")
                            SFTEval(hRequest(items), object : HApiCallback {
                                override fun hApiCallback(result: hReturn) {
                                    var image : Image = Image()
                                    image.url = downloadUri.toString()
                                    image.formality = result.formal[1]
                                    image.season = result.season
                                    image.trend = result.trend[0]
                                    image.value = convertValue(value.text.toString().toInt())

                                    ColorEval(wRequest(downloadUri.toString()), object: WApiCallback {
                                        override fun wApiCallback(result: wReturn) {
                                            image.colorfulness = result.values
                                            val values = listOf(image?.colorfulness, image!!.season[2], image.formality, image.trend, image.value)
                                            mainPentagon.background = PolygonalDrawable(5, values)
                                            mainPentagon.rotation = -90.0F
                                        }
                                    })
                                }
                            })
                        }
                    } else {
                        Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
            }
        }

        btnCamera.setOnClickListener {
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
    }


    fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    fun getFileExtension(uri: Uri): String? {
        val cR: ContentResolver = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    private fun post() {
        if (imageUri != null) {
            val fileReference = storageReference?.child(
                System.currentTimeMillis().toString() + "." + getFileExtension(imageUri!!)
            )

            val uploadTask = fileReference!!.putFile(imageUri!!)

            val urlTask = uploadTask.continueWithTask {
                if (!it.isSuccessful) {
                    throw it.exception!!
                }
                return@continueWithTask fileReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Upload successful", Toast.LENGTH_LONG).show()
                    val downloadUri = task.result
                    if (downloadUri != null) {

                        val id = documentReference!!.collection("clothes").document().id

                        val image = Image(url = downloadUri.toString())
                        val items = getItems()
//                        val items = listOf("マフラー", "手袋")
//                        SFTEval(image, items)

                        SFTEval(hRequest(items), object : HApiCallback {
                            override fun hApiCallback(result: hReturn) {
                                var image : Image = Image()
                                image.url = downloadUri.toString()
                                image.formality = result.formal[1]
                                image.season = result.season
                                image.trend = result.trend[0]
                                image.value = convertValue(value.text.toString().toInt())
                                documentReference!!.collection("clothes").document(id).set(image.toMap())


                                val post = Post(url = image.url, formality = image.formality, season = image.season,
                                                trend = image.trend, value = image.value, uploaded_at = image.uploaded_at, userId = userId as String)
                                clothesCol?.document(id)?.set(post.toMap())
                            }
                        })



//                        val url = "https://tshop.r10s.jp/decorative/cabinet/d2a/d2a154-1.jpg?fitin=275:275"
                        ColorEval(wRequest(downloadUri.toString()), object: WApiCallback {
                            override fun wApiCallback(result: wReturn) {
                                val document = documentReference!!.collection("clothes").document(id)
                                document.get().addOnSuccessListener {image ->
                                    val image = image.toObject(Image::class.java)
                                    image!!.colorfulness = result.values
                                    document.update(image.toMap())

                                    clothesCol?.document(id)?.get()?.addOnSuccessListener {post ->
                                        val post = post.toObject(Post::class.java)
                                        post!!.colorfulness = result.values
                                        clothesCol!!.document(id).update(post.toMap())
                                    }
                                }
                            }

                        })

                        val intent = Intent(this, Evaluation::class.java)
                        intent.putExtra(UPLOADED_IMAGE, id)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
                }
            }
//            fileReference!!.putFile(imageUri!!).addOnSuccessListener {
//                val handler = Handler()
//                handler.postDelayed({
//                    progressBar.progress = 0
//                }, 500)
//
//                Toast.makeText(this, "Upload successful", Toast.LENGTH_LONG).show()
//
//
//
//            }.addOnFailureListener {
//
//            }.addOnProgressListener {
//                val progress = (100.0 * it.bytesTransferred / it.totalByteCount)
//                progressBar.progress = progress.toInt()
//            }
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
        }
    }
    private fun convertValue(value : Int) : Int {
        val valueList = listOf(100000, 85000, 70000, 55000, 40000, 30000, 20000, 10000, 5000, 0)
        var range = 0
        for ((index, max) in valueList.withIndex()) {
            if (value >= max) {
                range = valueList.size - index
                break
            }
        }
        return range
    }

    private fun getItems(): List<String> {
        val itemList = mutableListOf<String>()
        for (x in 1..ITEM_SIZE) {
            val id = resources.getIdentifier("item$x", "id", packageName)
            val text = findViewById<TextView>(id).text.toString()
            if (text.isNotEmpty())
                itemList.add(text)
        }
        return itemList
    }

//    Asynchronous way
    interface HApiCallback {
        fun hApiCallback(result: hReturn)
    }
    fun SFTEval(request : hRequest, callback: HApiCallback) {
        val call : Call<hReturn>? = api?.SFTEval(request)

        call!!.enqueue(object : Callback<hReturn> {
            override fun onResponse(
                call: Call<hReturn>,
                response: Response<hReturn>
            ) {
                if (!response.isSuccessful) {
                    Toast.makeText(this@MainActivity, response.code().toString(), Toast.LENGTH_SHORT).show()
                    return
                }
                callback.hApiCallback(response.body()!!)

            }

            override fun onFailure(call: Call<hReturn>, t: Throwable) {
                Toast.makeText(this@MainActivity, t.message.toString(), Toast.LENGTH_SHORT).show()
            }

        })

    }

    //    Synchronous way (wait until request finishes and comes back with result)
//    fun SFTEval(image: Image, words: List<String>) {
//        val intent = Intent(this, HApiService::class.java)
//        intent.putExtra("image", image)
//        intent.putExtra("words", words.toTypedArray())
//        HApiService.enqueueWork(this, intent)
//
//    }

    interface WApiCallback {
        fun wApiCallback(result: wReturn)
    }

    fun ColorEval(request: wRequest, callback: WApiCallback) {
        val call: Call<wReturn> = api!!.ColorEval(request)
        call.enqueue(object : Callback<wReturn> {
            override fun onResponse(
                call: Call<wReturn>,
                response: Response<wReturn>
            ) {
                if (!response.isSuccessful) {
                    Toast.makeText(this@MainActivity, response.code().toString(), Toast.LENGTH_SHORT).show()
//                    textView1.text = response.code().toString()
                    return
                }
                callback.wApiCallback(response.body()!!)
            }

            override fun onFailure(call: Call<wReturn>, t: Throwable) {
                Toast.makeText(this@MainActivity, t.message.toString(), Toast.LENGTH_SHORT).show()
//                textView4.text = (t.message.toString())
            }

        })
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            Picasso.get().load(imageUri as Uri).into(imageView)
        } else if (requestCode == CAPTURE_REQUEST && resultCode == Activity.RESULT_OK) {
//            val takenImage = data?.extras?.get("data") as Bitmap

            imageUri = Uri.fromFile(photoFile)
            val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            imageView.setImageBitmap(takenImage)
        }
    }


//    override fun onStart() {
//        super.onStart()
//        documentReference!!.addSnapshotListener(this) { snapshot, e ->
//            if (e != null) {
//                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
//                return@addSnapshotListener
//            }
//
//            if (snapshot != null && snapshot.exists()) {
//                val user = snapshot.data!!.toObject<User>()
//                userName.text = user.name.toString()
//            }
//        }
//
//    }

//    fun getTaskList() {
//        val document = fStore!!.collection("users").document(userId.toString()).collection("tasks")
//            .orderBy("status", Query.Direction.DESCENDING)
//        document.get().addOnCompleteListener {
//            if (it.isSuccessful) {
//                taskList = it.result!!.toObjects(Todo::class.java)
//
//                for ((index, task) in it.result!!.withIndex()) {
//                    taskList[index].id = task.id
//                }
//
//                taskListAdapter.taskList = taskList
//                taskListAdapter.notifyDataSetChanged()
//            }
//        }
//    }

    fun onLogout(view: View) {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }

//    override fun invoke(p1: Todo) {
//        val taskId: String = p1.id!!
////        println(taskId)
//        val intent = Intent(this, editTask::class.java)
//        intent.putExtra(TodoList.SELECTED_TASK, taskId)
//        startActivity(intent)
//
//    }

}
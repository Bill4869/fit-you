package com.example.tohackmeapp
import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.firestore.Exclude
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.Serializable


data class User (
//    val id : String = "",
    var email : String = "",
    var name : String = "",
    var age : Int = 0,
    var description : String = "",
    var sex : String = "",
    var icon : String = "",
    var height : Int = 0,
    var weight : Int = 0,
    var posts : Int = 0,
    var created_at : Long = System.currentTimeMillis(),
    var updated_at : Long = System.currentTimeMillis()
)

 data class Image(
     var url: String = "",
     var colorfulness: Int = 0,
     var formality: Int = 0,
     var likes: Int = 0,
     var predictedLikes: Int = 0,
     var season: List<Int> = listOf(0,0,0,0),
     var trend: Int = 0,
     var value: Int = 0,
     var uploaded_at: Long = System.currentTimeMillis(),
     @get:Exclude
     var id: String? = null
) : Serializable

data class Post(
    var url: String = "",
    var colorfulness: Int = 0,
    var formality: Int = 0,
    var likes: Int = 0,
    var predictedLikes: Int = 0,
    var season: List<Int> = listOf(0,0,0,0),
    var trend: Int = 0,
    var value: Int = 0,
    var uploaded_at: Long = System.currentTimeMillis(),
    var userId: String = "",
    var item: List<Item> = listOf(Item()),
    @get:Exclude
    var id: String? = null
)

data class Item(
    val item_name : String = "アウター",
    val item_type : String = "ユニクロ",
    val value : Int = 5999
)

// API
data class hRequest(
    val words : List<String>
)

data class hReturn(
    var season: List<Int>,
    var formal: List<Int>,
    var trend: List<Int>
)

data class wRequest(
    val image_path: String
)
data class wReturn(
    val values: Int
)

data class TRequest1(
    val user_id : String,
    val image_path : String
)
data class TReturn1(
    val result : Int
)

data class  TRequest2(
    val user_id: String
)
data class  TReturn2(
    val user_id: String,
    val list: List<String>
)

//


//data class Todo (
//    var title : String = "",
//    var explanation : String = "",
//    var tag : String = "",
//    var level : Int? = null,
//    var status : Boolean = false,
//    @get:Exclude
//    var id : String? = null
//)
//
//data class Monster (
//    val id : Int = 1,
//    val name : String = "Penguin"
//)

object FormatConvertion {
    val gson = Gson()
    // Convert a Map to an object
    inline fun <reified T> Map<String, Any>.toObject(): T {
        return convert()
    }

    // Convert an object to a Map
    fun <T> T.toMap(): Map<String, Any> {
        return convert()
    }

    // Convert an object of type T to type R
    inline fun <T, reified R> T.convert(): R {
        val json = gson.toJson(this)
        println(json)
        return gson.fromJson(json, object : TypeToken<R>() {}.type)
    }
}
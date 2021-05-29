package de.hsrm.mi.mobilecomputing.bookworm.model

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import com.google.firebase.database.PropertyName
import org.json.JSONObject
import java.io.Serializable


data class Book (

    @set:PropertyName("Name")
    @get:PropertyName("Name")
    var name: String = "",
    @set:PropertyName("Author")
    @get:PropertyName("Author")
    var author: String = "",
    //lateinit var genre: String
    var isbn: String = "",
    @set:PropertyName("Cover")
    @get:PropertyName("Cover")
    var thumbail : String = "",
    @set:PropertyName("WishToRead")
    @get:PropertyName("WishToRead")
    var wishToRead : Boolean = false,
    @set:PropertyName("Reading")
    @get:PropertyName("Reading")
    var reading : Boolean = false,
    @set:PropertyName("FinishedReading")
    @get:PropertyName("FinishedReading")
    var readSuccessfully : Boolean = false,
    @set:PropertyName("Favorite")
    @get:PropertyName("Favorite")
    var favorite : Boolean = false,
    @set:PropertyName("Liked")
    @get:PropertyName("Liked")
    var liked : Boolean = false,
    @set:PropertyName("Disliked")
    @get:PropertyName("Disliked")
    var disliked : Boolean = false,
    @set:PropertyName("Lended")
    @get:PropertyName("Lended")
    var borrowed : Boolean = false
) : Serializable
{

    init {
        if(isbn != "") {
            val url = "https://www.googleapis.com/books/v1/volumes?q=isbn:$isbn"
            getDataFromISBN(url)
        }
    }

    fun getDataFromISBN(url: String){
        var responseAsJSON = JSONObject()
        Fuel.get(url)
            .responseJson { request, response, result ->
                responseAsJSON = JSONObject(result.get().content)

                try {
                    name = responseAsJSON
                        .getJSONArray("items")
                        .getJSONObject(0)
                        .getJSONObject("volumeInfo")
                        .get("title")
                        .toString()
                    author = responseAsJSON
                        .getJSONArray("items")
                        .getJSONObject(0)
                        .getJSONObject("volumeInfo")
                        .getJSONArray("authors")
                        .get(0)
                        .toString()
                } catch (e: Exception){
                    name = "Unknown"
                    author = "Unknown"
                }

                try {
                   thumbail = responseAsJSON
                        .getJSONArray("items")
                        .getJSONObject(0)
                        .getJSONObject("volumeInfo")
                        .getJSONObject("imageLinks")
                        .get("thumbnail")
                           .toString()
                } catch (e: Exception){
                    thumbail = "empty"
                }
            }
    }
}
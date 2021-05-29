package de.hsrm.mi.mobilecomputing.bookworm.model

import com.google.firebase.database.PropertyName

data class Lending(
        @set:PropertyName("ISBN")
        @get:PropertyName("ISBN")
        var isbn: String = "",
        @get:PropertyName("Date")
        @set:PropertyName("Date")
        var date: String = "",
        @get:PropertyName("UserUID")
        @set:PropertyName("UserUID")
        var userUID: String = "",
        @get:PropertyName("Title")
        @set:PropertyName("Title")
        var bookTitle: String = "",
        @get:PropertyName("Author")
        @set:PropertyName("Author")
        var bookAuthor: String = "",
        @get:PropertyName("User")
        @set:PropertyName("User")
        var userName: String = ""
)
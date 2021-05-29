package de.hsrm.mi.mobilecomputing.bookworm.model

import com.google.firebase.database.PropertyName

data class User(
        @set:PropertyName("Id")
        @get:PropertyName("Id")
        var id: String? = "",
        @get:PropertyName("UID")
        @set:PropertyName("UID")
        var UID: String = "",
        @get:PropertyName("E-Mail")
        @set:PropertyName("E-Mail")
        var email: String? = "",
        @get:PropertyName("Name")
        @set:PropertyName("Name")
        var name: String? = ""
)   {
        override fun toString(): String {
                return id.toString()
        }
}
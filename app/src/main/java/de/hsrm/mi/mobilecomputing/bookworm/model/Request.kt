package de.hsrm.mi.mobilecomputing.bookworm.model

import java.io.Serializable

data class Request(
    var user: User = User(),
    var book: Book = Book(),
    var returnBook: Boolean = false,
    var borrowBook: Boolean = false,
    var messageRead: Boolean = false
) : Serializable {
}
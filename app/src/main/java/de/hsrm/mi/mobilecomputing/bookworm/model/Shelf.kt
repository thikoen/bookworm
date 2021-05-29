package de.hsrm.mi.mobilecomputing.bookworm.model

data class Shelf(var books: List<Book>){

    fun addBook(book : Book){
        books.plus(book)
    }

}

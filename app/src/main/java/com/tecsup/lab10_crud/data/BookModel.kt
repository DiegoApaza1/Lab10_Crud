package com.tecsup.lab10_crud.data

import com.google.gson.annotations.SerializedName

data class BookModel(
    @SerializedName("id")
    var id: Int,
    @SerializedName("title")
    var title: String,
    @SerializedName("author")
    var author: String,
    @SerializedName("publication_date")
    var publication_date: String,
    @SerializedName("category")
    var category: String
)

package com.tecsup.lab10_crud.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BookApiService {
    @GET("books")
    suspend fun selectBooks(): ArrayList<BookModel>

    @GET("books/{id}")
    suspend fun selectBook(@Path("id") id: String): Response<BookModel>

    @Headers("Content-Type: application/json")
    @POST("books")
    suspend fun insertBook(@Body book: BookModel): Response<BookModel>

    @Headers("Content-Type: application/json")
    @PUT("books/{id}")
    suspend fun updateBook(@Path("id") id: String, @Body book: BookModel): Response<BookModel>

    @DELETE("books/{id}")
    suspend fun deleteBook(@Path("id") id: String): Response<Void>
}

package com.example.wodrunapp.service

import com.example.wodrunapp.Mouvements
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("wod.json")
    suspend fun getMouvements(): Response<MutableList<Mouvements>>

}
package ru.rpuxa.revoluttest.model

import retrofit2.http.GET
import retrofit2.http.Query

interface Server {

    @GET("latest")
    suspend fun getRates(@Query("base") base: String = Currency.BASE.name): GetRatesAnswer
}
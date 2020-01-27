package ru.rpuxa.revoluttest.dagger.providers

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.rpuxa.revoluttest.model.Server
import javax.inject.Singleton

@Module
class ServerProvider {

    @Provides
    @Singleton
    fun mainServer(): Server =
        Retrofit.Builder()
        .baseUrl("https://revolut.duckdns.org")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Server::class.java)

}
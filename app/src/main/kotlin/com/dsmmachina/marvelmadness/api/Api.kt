package com.dsmmachina.marvelmadness.api

import com.google.gson.GsonBuilder
import com.dsmmachina.marvelmadness.BuildConfig
import com.dsmmachina.marvelmadness.deserializers.CharactersDeserializer
import com.dsmmachina.marvelmadness.deserializers.ComicsDeserializer
import com.dsmmachina.marvelmadness.models.CharacterModel
import com.dsmmachina.marvelmadness.models.ComicModel
import io.reactivex.Observer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


object Api {

    private const val TIMESTAMP = 1

    private const val BASE_URL = "https://gateway.marvel.com/v1/public/"

    private const val CLIENT_SECRET_ERROR = "Please add client secrets before attempting to connect"

    private const val MAX_RESULTS = 50

    val isAuthorized : Boolean get(){
        return BuildConfig.API_KEY.isNotEmpty() &&
                BuildConfig.API_KEY.isNotBlank() &&
                BuildConfig.HASH.isNotEmpty() &&
                BuildConfig.HASH.isNotBlank()
    }


    // Get the current comics
    fun getComics(offset : Int?=null, limit : Int?=null, observer : Observer<Array<ComicModel>>) {

        if (isAuthorized) {

            val gsonBuilder = GsonBuilder()
            val deserializer = ComicsDeserializer()
            gsonBuilder.registerTypeAdapter(Array<ComicModel>::class.java, deserializer)

            val builder = Retrofit.Builder()
            builder.baseUrl(BASE_URL)

            val factory = GsonConverterFactory.create(gsonBuilder.create())
            builder.addConverterFactory(factory)

            val retrofit = builder.build()
            val service = retrofit.create(ApiService::class.java)
            val responseCall = service.getComics(
                    timestamp = TIMESTAMP.toString(),
                    apiKey = BuildConfig.API_KEY,
                    hash = BuildConfig.HASH,
                    offset = offset ?: 0,
                    limit = limit ?: MAX_RESULTS
            )

            responseCall.enqueue(object : Callback<Array<ComicModel>> {

                override fun onResponse(call: Call<Array<ComicModel>>, response: Response<Array<ComicModel>>) {

                    when(response.isSuccessful && response.code() == 200) {

                        true -> observer.onNext(response.body() ?: arrayOf())

                        false -> observer.onError(Throwable(response.message()))
                    }

                }

                override fun onFailure(call: Call<Array<ComicModel>>, t: Throwable) {
                    observer.onError(Throwable(t.message))
                }
            })
        }
        else {
            observer.onError(Throwable(CLIENT_SECRET_ERROR))
        }
    }

    // get a information about a specific character
    fun getCharacters(id : Long, observer : Observer<Array<CharacterModel>>) {

        if (isAuthorized) {

            val gsonBuilder = GsonBuilder()
            val deserializer = CharactersDeserializer()
            gsonBuilder.registerTypeAdapter(Array<CharacterModel>::class.java, deserializer)

            val builder = Retrofit.Builder()
            builder.baseUrl(BASE_URL)

            val factory = GsonConverterFactory.create(gsonBuilder.create())
            builder.addConverterFactory(factory)

            val retrofit = builder.build()
            val service = retrofit.create(ApiService::class.java)
            val responseCall = service.getCharacters(
                timestamp = TIMESTAMP.toString(),
                apiKey = BuildConfig.API_KEY,
                hash = BuildConfig.HASH,
                id = id.toString()
            )

            responseCall.enqueue(object : Callback<Array<CharacterModel>> {

                override fun onResponse(call: Call<Array<CharacterModel>>, response: Response<Array<CharacterModel>>) {

                    when(response.isSuccessful && response.code() == 200) {

                        true -> observer.onNext(response.body() ?: arrayOf())

                        false -> observer.onError(Throwable(response.message()))
                    }

                }

                override fun onFailure(call: Call<Array<CharacterModel>>, t: Throwable) {
                    observer.onError(Throwable(t.message))
                }
            })
        }
        else {
            observer.onError(Throwable(CLIENT_SECRET_ERROR))
        }
    }

}

interface ApiService {

    @GET("comics")
    fun getComics(
        @Query("ts") timestamp : String,
        @Query("apikey") apiKey : String,
        @Query("hash") hash : String,
        @Query("offset") offset : Int,
        @Query("limit") limit : Int,
    ): Call<Array<ComicModel>>

    @GET("comics/{id}/characters")
    fun getCharacters(
        @Path("id") id : String,
        @Query("ts") timestamp : String,
        @Query("apikey") apiKey : String,
        @Query("hash") hash : String,
    ): Call<Array<CharacterModel>>
}
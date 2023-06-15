package com.syukron.nutriary.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.syukron.nutriary.data.model.FoodList
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://nutriary-api-mgjpmn3hpa-et.a.run.app/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()


interface NutriaryApiService {
    @GET("nutrisi-specific/{query}")
    suspend fun getFoodList(@Path("query") query: String):
            FoodList
}

object RemoteDataSource {
    val httpClient: NutriaryApiService by lazy {
        retrofit.create(NutriaryApiService::class.java)
    }
}



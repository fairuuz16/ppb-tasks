package com.example.currencyconverter

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApiService {


    @GET("v6/latest/USD")
    suspend fun getLatestRates(): Response<ApiResponse>

     @GET("v6/latest/{baseCurrency}")
     suspend fun getRatesForBase(
         @Path("baseCurrency") baseCurrency: String
     ): Response<ApiResponse>
}
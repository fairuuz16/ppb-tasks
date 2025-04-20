package com.example.currencyconverter

import com.google.gson.annotations.SerializedName

// Represents the entire API response
data class ApiResponse(
    @SerializedName("result") val result: String,
    @SerializedName("base_code") val baseCode: String,
    @SerializedName("rates") val rates: Map<String, Double>,
    @SerializedName("time_last_update_utc") val lastUpdateUtc: String? = null
)
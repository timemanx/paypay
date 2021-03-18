package com.paypay.challenge.api

import com.paypay.challenge.api.response.ExchangeRateResponse
import retrofit2.http.GET

interface CurrencyLayerApi {

    @GET("/live")
    suspend fun rates(): ExchangeRateResponse
}
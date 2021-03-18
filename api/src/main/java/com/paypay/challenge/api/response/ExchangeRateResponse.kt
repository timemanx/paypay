package com.paypay.challenge.api.response

import com.paypay.challenge.api.json.ExchangeRateList
import com.paypay.challenge.api.model.ExchangeRate
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExchangeRateResponse(
    val success: Boolean,
    val terms: String,
    val privacy: String,
    val timestamp: Long,
    val source: String,
    @ExchangeRateList val quotes: List<ExchangeRate>
)
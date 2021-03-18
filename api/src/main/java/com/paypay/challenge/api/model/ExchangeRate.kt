package com.paypay.challenge.api.model

data class ExchangeRate(
    val source: String,
    val target: String,
    val rate: Float
)
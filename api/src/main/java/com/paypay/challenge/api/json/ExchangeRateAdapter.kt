package com.paypay.challenge.api.json

import com.paypay.challenge.api.model.ExchangeRate
import com.squareup.moshi.FromJson

import com.squareup.moshi.ToJson

object ExchangeRateAdapter {

    @ToJson
    fun toJson(@ExchangeRateList rates: List<ExchangeRate>): Map<String, Float> {
        return rates.map { "${it.source}${it.target}" to it.rate }.toMap()
    }

    /**
     * ISO 4217 currency codes are three letters as per specification.
     * https://www.iso.org/iso-4217-currency-codes.html
     *
     * So it's safe to extract the source and target currency codes by splitting the key string by length.
     */
    @FromJson
    @ExchangeRateList
    fun fromJson(rates: Map<String, Float>): List<ExchangeRate> {
        return rates.map {
            val sourceCurrency = it.key.substring(0..2)
            val targetCurrency = it.key.substring(3)
            ExchangeRate(sourceCurrency, targetCurrency, it.value)
        }
    }
}
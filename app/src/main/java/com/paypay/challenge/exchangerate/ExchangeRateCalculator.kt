package com.paypay.challenge.exchangerate

import com.paypay.challenge.api.model.ExchangeRate
import okhttp3.internal.toImmutableList

object ExchangeRateCalculator {

    fun calculateExchangeRates(
        amount: Float,
        sourceCurrency: String,
        targetCurrency: String,
        exchangeRateData: List<ExchangeRate>
    ): List<ExchangeRate>? {
        return if (sourceCurrency == targetCurrency) {
            exchangeRateData.map { ExchangeRate(it.source, it.target, amount * it.rate) }
        } else {
            exchangeRateData.find { it.target == targetCurrency }?.let { exchangeRate ->
                val reverseExchangeRate = 1 / exchangeRate.rate
                // Add source rate
                mutableListOf(ExchangeRate(targetCurrency, sourceCurrency, amount * reverseExchangeRate)).apply {
                    exchangeRateData.map { ExchangeRate(targetCurrency, it.target, amount * reverseExchangeRate * it.rate) }
                        .forEach {
                            if (it.source != it.target) {
                                add(it)
                            }
                        }
                }.toImmutableList()
            }
        }
    }
}
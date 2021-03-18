package com.paypay.challenge.exchangerate

import com.paypay.challenge.api.CurrencyLayerApi
import com.paypay.challenge.api.model.ExchangeRate
import com.paypay.challenge.api.response.ExchangeRateResponse
import com.paypay.challenge.storage.Storage

internal class ExchangeRateRepository(
    private val storage: Storage,
    private val api: CurrencyLayerApi
) {
    companion object {
        const val KEY_SOURCE_CURRENCY = "source_currency"
        const val KEY_EXCHANGE_RATES = "exchange_rates"
    }

    suspend fun getSourceCurrency(): String {
        return storage.get(KEY_SOURCE_CURRENCY)
            ?: api.rates().let {
                saveExchangeRateData(it)
                it.source
            }
    }

    suspend fun getExchangeRateData(): List<ExchangeRate> {
        return storage.get<String>(KEY_SOURCE_CURRENCY)?.let { source ->
            storage.get<Set<String>>(KEY_EXCHANGE_RATES)?.map { str ->
                str.split(":").let {
                    ExchangeRate(source, it[0], it[1].toFloat())
                }
            }
        } ?: api.rates().let {
            saveExchangeRateData(it)
            it.quotes
        }
    }

    suspend fun refreshExchangeRateData() {
        saveExchangeRateData(api.rates())
    }

    private fun saveExchangeRateData(response: ExchangeRateResponse) {
        storage.set(KEY_SOURCE_CURRENCY, response.source)
        storage.set(KEY_EXCHANGE_RATES, response.quotes.map { "${it.target}:${it.rate}" }.toSet())
    }
}
package com.paypay.challenge.exchangerate

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.paypay.challenge.api.model.ExchangeRate
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExchangeRateCalculatorTest {

    private val sourceCurrency = "USD"
    private val targetCurrency = "JPY"

    private val exchangeRateData = listOf(
        ExchangeRate("USD", "JPY", 100F),
        ExchangeRate("USD", "INR", 50F),
        ExchangeRate("USD", "CNY", 25F),
    )

    private val transformedExchangeRateData = listOf(
        ExchangeRate("JPY", "USD", 0.01F),
        ExchangeRate("JPY", "INR", 0.5F),
        ExchangeRate("JPY", "CNY", 0.25F),
    )

    @Test
    fun testCalculateExchangeRates() {
        assertThat(
            ExchangeRateCalculator.calculateExchangeRates(
                1F,
                sourceCurrency,
                sourceCurrency,
                exchangeRateData
            )
        ).isEqualTo(exchangeRateData)

        assertThat(
            ExchangeRateCalculator.calculateExchangeRates(
                3F,
                sourceCurrency,
                sourceCurrency,
                exchangeRateData
            )
        ).isEqualTo(exchangeRateData.map { ExchangeRate(it.source, it.target, it.rate * 3) })

        assertThat(
            ExchangeRateCalculator.calculateExchangeRates(
                1F,
                sourceCurrency,
                targetCurrency,
                exchangeRateData
            )
        ).isEqualTo(transformedExchangeRateData)

        assertThat(
            ExchangeRateCalculator.calculateExchangeRates(
                3F,
                sourceCurrency,
                targetCurrency,
                exchangeRateData
            )
        ).isEqualTo(transformedExchangeRateData.map { ExchangeRate(it.source, it.target, it.rate * 3) })
    }
}
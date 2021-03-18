package com.paypay.challenge.exchangerate

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.paypay.challenge.api.CurrencyLayerApi
import com.paypay.challenge.api.model.ExchangeRate
import com.paypay.challenge.api.response.ExchangeRateResponse
import com.paypay.challenge.storage.Storage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExchangeRateRepositoryTest {
    private val storage = mock<Storage>()

    private val api = mock<CurrencyLayerApi>()

    private val sourceCurrency = "USD"

    private val exchangeRates = listOf(
        ExchangeRate("USD", "JPY", 109.10F),
        ExchangeRate("USD", "INR", 72.50F),
        ExchangeRate("USD", "CNY", 59.10F),
    )

    private val rates = ExchangeRateResponse(
        true,
        "Terms",
        "Privacy",
        System.currentTimeMillis(),
        sourceCurrency,
        exchangeRates
    )

    @ExperimentalCoroutinesApi
    @Test
    fun testGetSourceCurrency_stored() {
        runBlockingTest {
            whenever(storage.get<String>(ExchangeRateRepository.KEY_SOURCE_CURRENCY)).thenReturn(sourceCurrency)
            assertThat(ExchangeRateRepository(storage, api).getSourceCurrency()).isEqualTo(sourceCurrency)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testGetSourceCurrency_notStored() {
        runBlockingTest {
            whenever(storage.get<String>(ExchangeRateRepository.KEY_SOURCE_CURRENCY)).thenReturn(null)
            whenever(api.rates()).thenReturn(rates)
            assertThat(ExchangeRateRepository(storage, api).getSourceCurrency()).isEqualTo(sourceCurrency)
            verify(storage).set(ExchangeRateRepository.KEY_SOURCE_CURRENCY, sourceCurrency)
            verify(storage).set(ExchangeRateRepository.KEY_EXCHANGE_RATES, exchangeRates.map { "${it.target}:${it.rate}" }.toSet())
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testGetExchangeRateData_notStored() {
        runBlockingTest {
            whenever(storage.get<String>(ExchangeRateRepository.KEY_SOURCE_CURRENCY)).thenReturn(null)
            whenever(storage.get<Set<String>>(ExchangeRateRepository.KEY_EXCHANGE_RATES)).thenReturn(null)
            whenever(api.rates()).thenReturn(rates)
            assertThat(ExchangeRateRepository(storage, api).getExchangeRateData()).isEqualTo(exchangeRates)
            verify(storage).set(ExchangeRateRepository.KEY_SOURCE_CURRENCY, sourceCurrency)
            verify(storage).set(ExchangeRateRepository.KEY_EXCHANGE_RATES, exchangeRates.map { "${it.target}:${it.rate}" }.toSet())
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testGetExchangeRateData_sourceCurrencyNotStored() {
        runBlockingTest {
            whenever(storage.get<String>(ExchangeRateRepository.KEY_SOURCE_CURRENCY)).thenReturn(null)
            whenever(storage.get<Set<String>>(ExchangeRateRepository.KEY_EXCHANGE_RATES)).thenReturn(emptySet())
            whenever(api.rates()).thenReturn(rates)
            assertThat(ExchangeRateRepository(storage, api).getExchangeRateData()).isEqualTo(exchangeRates)
            verify(storage).set(ExchangeRateRepository.KEY_SOURCE_CURRENCY, sourceCurrency)
            verify(storage).set(ExchangeRateRepository.KEY_EXCHANGE_RATES, exchangeRates.map { "${it.target}:${it.rate}" }.toSet())
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testGetExchangeRateData_exchangeRateDataNotStored() {
        runBlockingTest {
            whenever(storage.get<String>(ExchangeRateRepository.KEY_SOURCE_CURRENCY)).thenReturn(sourceCurrency)
            whenever(storage.get<Set<String>>(ExchangeRateRepository.KEY_EXCHANGE_RATES)).thenReturn(null)
            whenever(api.rates()).thenReturn(rates)
            assertThat(ExchangeRateRepository(storage, api).getExchangeRateData()).isEqualTo(exchangeRates)
            verify(storage).set(ExchangeRateRepository.KEY_SOURCE_CURRENCY, sourceCurrency)
            verify(storage).set(ExchangeRateRepository.KEY_EXCHANGE_RATES, exchangeRates.map { "${it.target}:${it.rate}" }.toSet())
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testGetExchangeRateData_stored() {
        runBlockingTest {
            whenever(storage.get<String>(ExchangeRateRepository.KEY_SOURCE_CURRENCY)).thenReturn(sourceCurrency)
            whenever(storage.get<Set<String>>(ExchangeRateRepository.KEY_EXCHANGE_RATES)).thenReturn(exchangeRates.map { "${it.target}:${it.rate}" }.toSet())
            verify(api, never()).rates()
            assertThat(ExchangeRateRepository(storage, api).getExchangeRateData()).isEqualTo(exchangeRates)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testRefreshExchangeRateData() {
        runBlockingTest {
            whenever(api.rates()).thenReturn(rates)
            verify(storage).set(ExchangeRateRepository.KEY_SOURCE_CURRENCY, sourceCurrency)
            verify(storage).set(ExchangeRateRepository.KEY_EXCHANGE_RATES, exchangeRates.map { "${it.target}:${it.rate}" }.toSet())
        }
    }
}
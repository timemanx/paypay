package com.paypay.challenge.exchangerate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.paypay.challenge.api.model.ExchangeRate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExchangeRateViewModelTest {
    private val repository = mock<ExchangeRateRepository>()

    private val savedStateHandle = mock<SavedStateHandle>()

    private val currencyLiveData = mock<MutableLiveData<String>>()

    val exchangeRateData = listOf(
        ExchangeRate("USD", "JPY", 109.10F),
        ExchangeRate("USD", "INR", 72.50F),
        ExchangeRate("USD", "CNY", 59.10F),
    )
    val sourceCurrency = "USD"

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        whenever(savedStateHandle.get<Float>(ExchangeRateViewModel.KEY_AMOUNT)).thenReturn(null)
        whenever(savedStateHandle.getLiveData<String>(ExchangeRateViewModel.KEY_CURRENCY)).thenReturn(currencyLiveData)
        runBlockingTest {
            whenever(repository.getExchangeRateData()).thenReturn(exchangeRateData)
            whenever(repository.getSourceCurrency()).thenReturn(sourceCurrency)
        }
        whenever(currencyLiveData.value).thenReturn("JPY")
    }

    @Test
    fun testGetAmount_defaultValue() {
        whenever(savedStateHandle.get<Float>(ExchangeRateViewModel.KEY_AMOUNT)).thenReturn(null)
        val viewModel = ExchangeRateViewModel(repository, savedStateHandle)
        assertThat(viewModel.amount).isEqualTo(1)
    }

    @Test
    fun testGetAmount_modifiedValue() {
        whenever(savedStateHandle.get<Float>(ExchangeRateViewModel.KEY_AMOUNT)).thenReturn(100F)
        val viewModel = ExchangeRateViewModel(repository, savedStateHandle)
        assertThat(viewModel.amount).isEqualTo(100F)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testGetCurrency_defaultValue() {
        whenever(currencyLiveData.value).thenReturn(null)
        val viewModel = ExchangeRateViewModel(repository, savedStateHandle)
        verify(viewModel.currency).value = sourceCurrency
    }

    @Test
    fun testGetCurrency_modifiedValue() {
        val viewModel = ExchangeRateViewModel(repository, savedStateHandle)
        assertThat(viewModel.currency.value).isEqualTo("JPY")
    }

    @Test
    fun testSetCurrency() {
        whenever(currencyLiveData.value).thenReturn(sourceCurrency)
        val viewModel = ExchangeRateViewModel(repository, savedStateHandle)
        assertThat(viewModel.currency.value).isEqualTo(sourceCurrency)
        assertThat(viewModel.rates.value).isEqualTo(exchangeRateData.sortedBy { it.target })
        assertThat(viewModel.isSuccess.value).isTrue()
        assertThat(viewModel.isLoading.value).isFalse()
    }

    @Test
    fun testRates_defaultValue() {
        whenever(currencyLiveData.value).thenReturn(null)
        val viewModel = ExchangeRateViewModel(repository, savedStateHandle)
        assertThat(viewModel.rates.value).isEqualTo(exchangeRateData.sortedBy { it.target })
    }

    @Test
    fun testRates_modifiedValue() {
        whenever(currencyLiveData.value).thenReturn("JPY")
        val viewModel = ExchangeRateViewModel(repository, savedStateHandle)
        assertThat(viewModel.rates.value).isEqualTo(
            ExchangeRateCalculator.calculateExchangeRates(1F, "USD", "JPY", exchangeRateData)?.sortedBy { it.target }
        )
    }

    @Test
    fun testIsLoading() {
        val viewModel = ExchangeRateViewModel(repository, savedStateHandle)
        assertThat(viewModel.isLoading.value).isFalse()
    }

    @Test
    fun testIsSuccess_onSuccess() {
        val viewModel = ExchangeRateViewModel(repository, savedStateHandle)
        assertThat(viewModel.isSuccess.value).isTrue()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testIsSuccess_onError() {
        runBlockingTest {
            whenever(repository.getExchangeRateData()).thenThrow(IllegalArgumentException())
            val viewModel = ExchangeRateViewModel(repository, savedStateHandle)
            assertThat(viewModel.isSuccess.value).isFalse()
            assertThat(viewModel.isLoading.value).isFalse()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testGetCurrencies() {
        runBlockingTest {
            whenever(currencyLiveData.value).thenReturn(null)
            whenever(repository.getExchangeRateData()).thenReturn(exchangeRateData)
            val viewModel = ExchangeRateViewModel(repository, savedStateHandle)
            assertThat(viewModel.currencies).isEqualTo(
                ExchangeRateCalculator.calculateExchangeRates(1F, "USD", "USD", exchangeRateData)?.sortedBy { it.target }?.map { it.target }
            )
        }
    }

}
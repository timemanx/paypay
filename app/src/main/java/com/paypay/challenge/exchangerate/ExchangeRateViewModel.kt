package com.paypay.challenge.exchangerate

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.paypay.challenge.api.model.ExchangeRate
import kotlinx.coroutines.launch

internal class ExchangeRateViewModel(
    private val exchangeRateRepository: ExchangeRateRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        const val KEY_AMOUNT = "amount"
        const val KEY_CURRENCY = "currency"
    }

    var amount = savedStateHandle.get<Float>(KEY_AMOUNT) ?: 1F
        set(value) {
            field = value
            savedStateHandle[KEY_AMOUNT] = value
            refreshExchangeRates()
        }

    val currency = savedStateHandle.getLiveData<String>(KEY_CURRENCY)

    val rates = MutableLiveData<List<ExchangeRate>>()

    val isLoading = MutableLiveData<Boolean>()

    val isSuccess = MutableLiveData<Boolean>()

    val currencies: List<String>
        get() = rates.value?.map { it.target } ?: emptyList()

    init {
        refreshExchangeRates()
    }

    fun setCurrency(currency: String) {
        this.currency.value = currency
        refreshExchangeRates()
    }

    private fun refreshExchangeRates() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val rateData = exchangeRateRepository.getExchangeRateData()
                val sourceCurrency = exchangeRateRepository.getSourceCurrency()
                val targetCurrency = currency.value ?: sourceCurrency.also { currency.value = sourceCurrency }
                rates.value = ExchangeRateCalculator.calculateExchangeRates(amount, sourceCurrency, targetCurrency, rateData)?.sortedBy { it.target }
                isSuccess.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                isSuccess.value = false
            } finally {
                isLoading.value = false
            }
        }
    }

    class Factory(
        private val exchangeRateRepository: ExchangeRateRepository,
        savedStateRegistryOwner: SavedStateRegistryOwner
    ): AbstractSavedStateViewModelFactory(savedStateRegistryOwner, null) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
            return ExchangeRateViewModel(exchangeRateRepository, handle) as T
        }
    }
}
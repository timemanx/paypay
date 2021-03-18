package com.paypay.challenge.exchangerate

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import com.paypay.challenge.R
import com.paypay.challenge.databinding.ActivityExchangeRateBinding
import com.paypay.challenge.network.Api
import com.paypay.challenge.storage.StorageProvider

class ExchangeRateActivity: AppCompatActivity() {

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityExchangeRateBinding>(this, R.layout.activity_exchange_rate)
    }

    private val viewModel by viewModels<ExchangeRateViewModel> {
        val storage = StorageProvider.getStorage(this)
        val currencyLayerApi = Api.currencyLayerApi
        val exchangeRateRepository = ExchangeRateRepository(storage, currencyLayerApi)
        // TODO Would instead use dependency injection in the ViewModel in a larger project
        ExchangeRateViewModel.Factory(exchangeRateRepository, this)
    }

    private val adapter = ExchangeRateAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this
        binding.isLoading = viewModel.isLoading
        binding.isSuccess = viewModel.isSuccess
        binding.amount.setText(getString(R.string.amount_text, viewModel.amount))
        binding.exchangeRates.adapter = this.adapter

        binding.amount.doAfterTextChanged {
            viewModel.amount = it?.toString()?.toFloatOrNull() ?: 1F
        }

        binding.currency.setOnClickListener {
            viewModel.currencies
                .takeIf { it.isNotEmpty() }
                ?.let {
                    CurrencySelectionDialog(it) { currency ->
                        viewModel.setCurrency(currency)
                    }.show(supportFragmentManager, "currency_dialog")
                }
        }

        observeData()
    }

    private fun observeData() {
        viewModel.currency.observe(this) {
            binding.currency.text = it
        }

        viewModel.rates.observe(this) {
            adapter.setRates(it)
        }
    }
}
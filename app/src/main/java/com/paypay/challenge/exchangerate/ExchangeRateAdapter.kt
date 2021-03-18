package com.paypay.challenge.exchangerate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paypay.challenge.api.model.ExchangeRate
import com.paypay.challenge.databinding.RowExchangeRateBinding

internal class ExchangeRateAdapter : RecyclerView.Adapter<ExchangeRateAdapter.ExchangeRateViewHolder>() {
    private val exchangeRates = mutableListOf<ExchangeRate>()

    fun setRates(rates: List<ExchangeRate>) {
        exchangeRates.clear()
        exchangeRates.addAll(rates)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeRateViewHolder {
        val binding = RowExchangeRateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExchangeRateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExchangeRateViewHolder, position: Int) {
        holder.update(exchangeRates[position])
    }

    override fun getItemCount(): Int {
        return exchangeRates.size
    }

    internal class ExchangeRateViewHolder(
        private val binding: RowExchangeRateBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun update(exchangeRate: ExchangeRate) {
            binding.exchangeRate = exchangeRate
            binding.executePendingBindings()
        }
    }
}
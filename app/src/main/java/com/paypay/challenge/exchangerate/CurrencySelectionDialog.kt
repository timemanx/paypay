package com.paypay.challenge.exchangerate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.paypay.challenge.R

internal class CurrencySelectionDialog(
    private val currencyList: List<String>,
    private val onClick: ((String) -> Unit)?
) : BottomSheetDialogFragment() {

    constructor() : this(emptyList(), null) {
        dismiss()
    }

    override fun onStart() {
        super.onStart()
        (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        return RecyclerView(inflater.context).apply {
            layoutManager = LinearLayoutManager(inflater.context)
            adapter = CurrencyListAdapter(currencyList) {
                onClick?.invoke(it)
                dismiss()
            }
        }
    }

    private class CurrencyListAdapter(
        private val currencyList: List<String>,
        private val onClick: ((String) -> Unit)
    ) : RecyclerView.Adapter<CurrencyListAdapter.CurrencyViewHolder>() {

        override fun getItemCount(): Int {
            return currencyList.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
            val view = TextView(parent.context).apply {
                setPadding(resources.getDimensionPixelSize(R.dimen.padding_currency_option))
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
            }
            return CurrencyViewHolder(view, onClick)
        }

        override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
            holder.currency = currencyList[position]
        }

        private class CurrencyViewHolder(
            val textView: TextView,
            onClick: ((String) -> Unit)
        ) : RecyclerView.ViewHolder(textView) {
            var currency: String? = null
                set(value) {
                    field = value
                    textView.text = value
                }

            init {
                textView.setOnClickListener {
                    currency?.let {
                        onClick.invoke(it)
                    }
                }
            }
        }
    }
}
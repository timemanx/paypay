package com.paypay.challenge.network

import com.paypay.challenge.BuildConfig
import com.paypay.challenge.api.CurrencyLayerApi
import com.paypay.challenge.api.json.ExchangeRateAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.addAdapter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

internal object Api {
    private const val accessKey = BuildConfig.ACCESS_KEY
    private const val baseUrl = BuildConfig.BASE_URL

    private val okHttpClient = OkHttpClient.Builder().run {
        addInterceptor(AuthInterceptor(accessKey))

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            addInterceptor(logging)
        }

        build()
    }

    private val moshi = Moshi.Builder()
        .add(ExchangeRateAdapter)
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okHttpClient)
        .baseUrl(baseUrl)
        .build()

    val currencyLayerApi = retrofit.create(CurrencyLayerApi::class.java)
}
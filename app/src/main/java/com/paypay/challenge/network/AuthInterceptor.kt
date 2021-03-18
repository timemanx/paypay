package com.paypay.challenge.network

import okhttp3.Interceptor
import okhttp3.Response

internal class AuthInterceptor(private val accessKey: String): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val url = request.url
            .newBuilder()
            .addQueryParameter("access_key", accessKey)
            .build()

        return chain.proceed(request.newBuilder().url(url).build())
    }
}
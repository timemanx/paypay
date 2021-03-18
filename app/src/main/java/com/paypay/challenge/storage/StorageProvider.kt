package com.paypay.challenge.storage

import android.content.Context

internal object StorageProvider {

    fun getStorage(context: Context): Storage {
        return DefaultStorage(context)
    }
}
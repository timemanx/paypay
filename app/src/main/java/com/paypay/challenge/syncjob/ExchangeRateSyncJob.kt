package com.paypay.challenge.syncjob

import android.app.job.JobParameters
import android.app.job.JobService
import com.paypay.challenge.exchangerate.ExchangeRateRepository
import com.paypay.challenge.network.Api
import com.paypay.challenge.storage.StorageProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class ExchangeRateSyncJob: JobService() {
    private val job = Job()
    private val coroutineScope = Dispatchers.Main + job

    private val exchangeRateRepository by lazy {
        ExchangeRateRepository(StorageProvider.getStorage(this), Api.currencyLayerApi)
    }

    private val syncJobScheduler by lazy {
        SyncJobScheduler(this)
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        CoroutineScope(coroutineScope).launch {
            try {
                exchangeRateRepository.refreshExchangeRateData()
                syncJobScheduler.schedule()
                jobFinished(params, false)
            } catch (e: Exception) {
                e.printStackTrace()
                jobFinished(params, true)
            }
        }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
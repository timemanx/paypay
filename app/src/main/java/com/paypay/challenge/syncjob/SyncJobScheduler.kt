package com.paypay.challenge.syncjob

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import androidx.core.content.getSystemService
import java.util.concurrent.TimeUnit

internal class SyncJobScheduler(private val context: Context) {

    val isScheduled = context.getSystemService<JobScheduler>()?.allPendingJobs?.size != 0

    @Synchronized
    fun schedule() {
        val serviceComponent = ComponentName(context, ExchangeRateSyncJob::class.java)
        val builder = JobInfo.Builder(0, serviceComponent)
            .setMinimumLatency(TimeUnit.MINUTES.toMillis(30))
        context.getSystemService<JobScheduler>()?.schedule(builder.build())
    }
}
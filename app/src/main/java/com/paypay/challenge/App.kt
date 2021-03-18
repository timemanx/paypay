package com.paypay.challenge

import android.app.Application
import com.paypay.challenge.syncjob.SyncJobScheduler

class App: Application() {
    private lateinit var syncJobScheduler: SyncJobScheduler

    override fun onCreate() {
        super.onCreate()
        initializeSyncJob()
    }

    private fun initializeSyncJob() {
        syncJobScheduler = SyncJobScheduler(applicationContext)
        if (!syncJobScheduler.isScheduled) {
            syncJobScheduler.schedule()
        }
    }
}
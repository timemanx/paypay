package com.paypay.challenge.syncjob

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            SyncJobScheduler(context).schedule()
        }
    }
}
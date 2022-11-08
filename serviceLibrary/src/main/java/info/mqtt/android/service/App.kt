package info.mqtt.android.service

import android.app.Application
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory

interface IMAPP : Configuration.Provider {
    override fun getWorkManagerConfiguration(): Configuration {
        val workerFactory = DelegatingWorkerFactory()
//        workerFactory.addFactory()
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()
    }

}
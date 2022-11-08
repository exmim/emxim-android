package info.mqtt.android.service.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

internal class WorkPingSender(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams){
    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }
}
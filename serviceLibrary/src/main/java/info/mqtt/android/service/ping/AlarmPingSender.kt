package info.mqtt.android.service.ping

import android.content.Context
import android.util.Log
import androidx.work.*
import info.mqtt.android.service.MqttService
import kotlinx.coroutines.suspendCancellableCoroutine
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttPingSender
import org.eclipse.paho.client.mqttv3.internal.ClientComms
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.properties.Delegates


/**
 * Default ping sender implementation on Android. It is based on AlarmManager.
 *
 * This class implements the [MqttPingSender] ping interface
 * allowing applications to send ping packet to server every keep alive interval.
 *
 * @see MqttPingSender
 */
internal class AlarmPingSender(val service: MqttService) : MqttPingSender {
    private val workManager = WorkManager.getInstance(service)

    override fun init(comms: ClientComms) {
        clientComms = comms
    }

    override fun start() {
        clientComms?.apply {
            schedule(keepAlive)
        }
    }

    override fun stop() {
        workManager.cancelUniqueWork(PING_JOB)
    }

    override fun schedule(delayInMilliseconds: Long) {
        Timber.d("Schedule next alarm at ${System.currentTimeMillis() + delayInMilliseconds}")
        workManager.enqueueUniqueWork(
            PING_JOB,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            OneTimeWorkRequest
                .Builder(PingWorker::class.java)
                .setInitialDelay(delayInMilliseconds, TimeUnit.MILLISECONDS)
                .build()
        )
    }

    companion object {
        private const val PING_JOB = "PING_JOB"
        private var clientComms: ClientComms? = null
    }

    internal class PingWorker(context: Context, workerParams: WorkerParameters) :
        CoroutineWorker(context, workerParams) {
        override suspend fun doWork(): Result =
            suspendCancellableCoroutine { continuation ->
                Timber.d("Sending Ping at: ${System.currentTimeMillis()}")
                clientComms?.checkForActivity(object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Timber.d("Success.")
                        continuation.resume(Result.success())
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Timber.d("Failure.")
                        exception?.printStackTrace()
                        continuation.resume(Result.failure())
                    }
                }) ?: kotlin.run {
                    continuation.resume(Result.failure())
                }
            }
    }

}

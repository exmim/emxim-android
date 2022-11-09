package com.skipper.exmim_lib

import android.content.Context
import android.content.Intent
import android.util.Log
import com.skipper.exmim_lib.log.logd
import com.skipper.exmim_lib.log.loge
import com.skipper.exmim_lib.notif.Notify
import com.skipper.exmim_lib.pojo.ImMessage
import com.skipper.exmim_lib.pojo.obtain
import info.mqtt.android.service.Ack
import info.mqtt.android.service.MqttAndroidClient
import info.mqtt.android.service.MqttTraceHandler
import info.mqtt.android.service.QoS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.*
import timber.log.Timber
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.properties.Delegates

enum class ClientType(val type: String){
    PHONE("phone"),
    PC("pc"),
    PAD("pad")
}

interface IMClient {
    fun connect(imAccount: IMAccount, clientType: ClientType, imConnectListener: IMConnectListener)
    fun disconnect()

    fun publish(topic: String, imMessage: ImMessage, qos: QoS = QoS.AtLeastOnce)
    fun publish(topic: String, imMessage: ImMessage, qos: QoS = QoS.AtLeastOnce, retain: Boolean = true)
    fun subscribe(topic: String, qos: QoS = QoS.AtLeastOnce)
    fun subscribe(topics: Array<String>, qos: EnumSet<QoS>)

    fun addCallback(imCallback: IMCallback)
    fun setCallback(imCallback: IMCallback)

    companion object {
        private val clientCache = ConcurrentHashMap<String, IMClient>()
        private var mAppContext by Delegates.notNull<Context>()
        var mMainActivity by Delegates.notNull<Class<*>>()
        fun init(appContext: Context, mainActivity: Class<*>) {
            mAppContext = appContext
            mMainActivity = mainActivity
            Timber.plant(Timber.DebugTree())
        }
        fun get(url: String): IMClient {
            return if (clientCache.containsKey(url)) {
                var client = clientCache[url]
                if (client == null) {
                    client = IMClientImp(mAppContext, url)
                    clientCache[url] = client
                    client
                } else {
                    client
                }
            } else {
                val client = IMClientImp(mAppContext, url)
                clientCache[url] = client
                client
            }
        }

        fun removeCache(url: String) {
            clientCache.remove(url)
        }

        private fun clear() {
            clientCache.clear()
        }

        fun release() {
            clientCache.forEach { it.value.disconnect() }
            clientCache.clear()
        }
    }
}

interface IMCallback {
    fun messageArrived(topic: String?, message: ImMessage)
}

interface IMConnectListener{
    fun connected(clientId: String)
    fun connectFailed(clientId: String, exception: Throwable?)
}

data class IMAccount(
    val userName: String,
    val password: String
)

private class IMClientImp(private val context: Context, private val url: String) : IMClient {

    private var delegate by Delegates.notNull<MqttAndroidClient>()

    private val callbacks = mutableListOf<IMCallback>()

    private fun createClient(context: Context, url: String, clientId: String): MqttAndroidClient = MqttAndroidClient(context, url, clientId, Ack.AUTO_ACK).apply {
        val intent = Intent(context, IMClient.mMainActivity).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val foregroundNotification = Notify.foregroundNotification(context, clientId, intent, R.string.notifyForeground)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            setForegroundService(foregroundNotification, 77)
    }

    override fun connect(imAccount: IMAccount, clientType: ClientType, imConnectListener: IMConnectListener) {
        delegate = createClient(context, url, imAccount.userName+"_android"+"_${clientType.type}")
        delegate.setTraceCallback(object : MqttTraceHandler {
            override fun traceDebug(message: String?) {
                logd(message)
            }

            override fun traceError(message: String?) {
                loge(message)
            }

            override fun traceException(message: String?, e: Exception?) {
                e?.printStackTrace()
                loge(message)
            }
        })
        delegate.setCallback(object : MqttCallbackExtended {
            override fun connectionLost(cause: Throwable?) {
                logd("connectLost")

            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                try {
                    logd("arrived: topic: ${topic}, message: ${message?.obtain() ?: ""}")
                    if (message != null) {
                        callbacks.forEach {
                            it.messageArrived(topic, message.obtain())
                        }
                    }
                } catch (e: Exception) {}
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                try {
                    logd("delivery: ${token?.message?.obtain()}")
                    token?.message?.apply {
                        messageDelivery(this)
                    }
                } catch (e: Exception) { }
            }

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                logd("reconnect: $reconnect")
                if (reconnect) {
                    // resend message
                    sendFailedMessage()
                }
            }

        })
        delegate.connect(configConnectOptions(imAccount), null, callback = object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                logd("connected: ${delegate.clientId}")
                delegate.subscribe("client/${delegate.clientId}", QoS.AtLeastOnce.value)
                delegate.subscribe("client/${imAccount.userName}", QoS.AtLeastOnce.value)
                delegate.subscribe("client/${imAccount.userName}_android", QoS.AtLeastOnce.value)
                syncMessage(imAccount)
                syncTopic(imAccount)
                syncGroup(imAccount)
                syncImUsers(imAccount)
                imConnectListener.connected(delegate.clientId)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
//                exception?.printStackTrace()
                imConnectListener.connectFailed(delegate.clientId, exception)
            }
        })
    }



    private fun configConnectOptions(imAccount: IMAccount): MqttConnectOptions = MqttConnectOptions().also {
        it.isAutomaticReconnect = true
        it.isCleanSession = false
        it.userName = imAccount.userName
        it.password = imAccount.password.toCharArray()
    }


    override fun disconnect() {
        delegate.disconnect()
        callbacks.clear()
    }

    override fun publish(topic: String, imMessage: ImMessage, qos: QoS) {
        //cache message to db
        publish(topic, imMessage, qos, true)
    }

    override fun publish(topic: String, imMessage: ImMessage, qos: QoS, retain: Boolean) {
        //cache message to db

        delegate.publish(topic, imMessage.obtain(qos, retain), null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                // mark message send success
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                // mark message send failed
            }
        })
    }

    override fun subscribe(topic: String, qos: QoS) {
        delegate.subscribe(topic, qos.value)
    }

    override fun subscribe(topics: Array<String>, qos: EnumSet<QoS>) {
        delegate.subscribe(topics, qos.map { it.value }.toIntArray())
    }

    override fun addCallback(imCallback: IMCallback) {
        callbacks.add(imCallback)
    }

    override fun setCallback(imCallback: IMCallback) {
        callbacks.clear()
        callbacks.add(imCallback)
    }

    private fun sendFailedMessage() {
//        CoroutineScope(Dispatchers.IO).launch {
//            //load failed message
//
//        }
    }

    private fun messageDelivery(mqttMessage: MqttMessage) {
        val imMessage = mqttMessage.obtain()
        // mark message delivery
    }

    private fun saveMessageToDb(imMessage: ImMessage) {
//        CoroutineScope(Dispatchers.IO).launch {
//            imMessage.saveToDb()
//        }
    }

    private fun syncMessage(account: IMAccount) {
        //between last status success message createAt and System.currentTimeMillis()

    }

    private fun syncTopic(account: IMAccount) {
        //between last status success message createAt and System.currentTimeMillis()
    }

    private fun syncGroup(account: IMAccount) {

    }

    private fun syncImUsers(account: IMAccount) {

    }
}
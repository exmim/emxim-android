package info.mqtt.android.service.ping;

import java.lang.System;

/**
 * Default ping sender implementation on Android. It is based on AlarmManager.
 *
 * This class implements the [MqttPingSender] ping interface
 * allowing applications to send ping packet to server every keep alive interval.
 *
 * @see MqttPingSender
 */
@kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0005\b\u0000\u0018\u0000 \u00122\u00020\u0001:\u0002\u0012\u0013B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0010\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\b\u0010\u0010\u001a\u00020\nH\u0016J\b\u0010\u0011\u001a\u00020\nH\u0016R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Linfo/mqtt/android/service/ping/AlarmPingSender;", "Lorg/eclipse/paho/client/mqttv3/MqttPingSender;", "service", "Linfo/mqtt/android/service/MqttService;", "(Linfo/mqtt/android/service/MqttService;)V", "getService", "()Linfo/mqtt/android/service/MqttService;", "workManager", "Landroidx/work/WorkManager;", "init", "", "comms", "Lorg/eclipse/paho/client/mqttv3/internal/ClientComms;", "schedule", "delayInMilliseconds", "", "start", "stop", "Companion", "PingWorker", "serviceLibrary_debug"})
public final class AlarmPingSender implements org.eclipse.paho.client.mqttv3.MqttPingSender {
    @org.jetbrains.annotations.NotNull()
    private final info.mqtt.android.service.MqttService service = null;
    private final androidx.work.WorkManager workManager = null;
    @org.jetbrains.annotations.NotNull()
    public static final info.mqtt.android.service.ping.AlarmPingSender.Companion Companion = null;
    private static final java.lang.String PING_JOB = "PING_JOB";
    private static org.eclipse.paho.client.mqttv3.internal.ClientComms clientComms;
    
    public AlarmPingSender(@org.jetbrains.annotations.NotNull()
    info.mqtt.android.service.MqttService service) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final info.mqtt.android.service.MqttService getService() {
        return null;
    }
    
    @java.lang.Override()
    public void init(@org.jetbrains.annotations.NotNull()
    org.eclipse.paho.client.mqttv3.internal.ClientComms comms) {
    }
    
    @java.lang.Override()
    public void start() {
    }
    
    @java.lang.Override()
    public void stop() {
    }
    
    @java.lang.Override()
    public void schedule(long delayInMilliseconds) {
    }
    
    @kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0000\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0011\u0010\u0007\u001a\u00020\bH\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\t\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\n"}, d2 = {"Linfo/mqtt/android/service/ping/AlarmPingSender$PingWorker;", "Landroidx/work/CoroutineWorker;", "context", "Landroid/content/Context;", "workerParams", "Landroidx/work/WorkerParameters;", "(Landroid/content/Context;Landroidx/work/WorkerParameters;)V", "doWork", "Landroidx/work/ListenableWorker$Result;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "serviceLibrary_debug"})
    public static final class PingWorker extends androidx.work.CoroutineWorker {
        
        public PingWorker(@org.jetbrains.annotations.NotNull()
        android.content.Context context, @org.jetbrains.annotations.NotNull()
        androidx.work.WorkerParameters workerParams) {
            super(null, null);
        }
        
        @org.jetbrains.annotations.Nullable()
        @java.lang.Override()
        public java.lang.Object doWork(@org.jetbrains.annotations.NotNull()
        kotlin.coroutines.Continuation<? super androidx.work.ListenableWorker.Result> continuation) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Linfo/mqtt/android/service/ping/AlarmPingSender$Companion;", "", "()V", "PING_JOB", "", "clientComms", "Lorg/eclipse/paho/client/mqttv3/internal/ClientComms;", "serviceLibrary_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}
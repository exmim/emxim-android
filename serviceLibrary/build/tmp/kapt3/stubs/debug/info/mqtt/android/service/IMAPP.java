package info.mqtt.android.service;

import java.lang.System;

@kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H\u0016\u00a8\u0006\u0004"}, d2 = {"Linfo/mqtt/android/service/IMAPP;", "Landroidx/work/Configuration$Provider;", "getWorkManagerConfiguration", "Landroidx/work/Configuration;", "serviceLibrary_debug"})
public abstract interface IMAPP extends androidx.work.Configuration.Provider {
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public abstract androidx.work.Configuration getWorkManagerConfiguration();
    
    @kotlin.Metadata(mv = {1, 6, 0}, k = 3)
    public final class DefaultImpls {
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public static androidx.work.Configuration getWorkManagerConfiguration(@org.jetbrains.annotations.NotNull()
        info.mqtt.android.service.IMAPP $this) {
            return null;
        }
    }
}
package com.skipper.exmim

import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Messenger
import android.util.Log
import com.shihoo.daemon.work.AbsWorkService
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit

class MainWorkService : AbsWorkService() {
    private var mDisposable: Disposable? = null
    private var mSaveDataStamp: Long = 0

    /**
     * 是否 任务完成, 不再需要服务运行?
     * @return 应当停止服务, true; 应当启动服务, false; 无法判断, 什么也不做, null.
     */
    override fun needStartWorkService(): Boolean {
        return true
    }//若还没有取消订阅, 就说明任务仍在运行.

    /**
     * 任务是否正在运行?
     * @return 任务正在运行, true; 任务当前不在运行, false; 无法判断, 什么也不做, null.
     */
    override fun isWorkRunning(): Boolean = mDisposable?.isDisposed == false

    override fun onBindService(intent: Intent?, v: Void?): IBinder {
        // 此处必须有返回，否则绑定无回调
        return Messenger(Handler()).binder
    }

    override fun onServiceKilled() {
        saveData()
        Log.d("wsh-daemon", "onServiceKilled --- 保存数据到磁盘")
    }

    override fun stopWork() {
        //取消对任务的订阅
        if (mDisposable?.isDisposed == false) {
            mDisposable?.dispose()
        }
        saveData()
    }


    override fun startWork() {
        Log.d("wsh-daemon", "检查磁盘中是否有上次销毁时保存的数据")
        mDisposable = Observable
            .interval(3, TimeUnit.SECONDS) //取消任务时取消定时唤醒
            .doOnDispose {
                Log.d("wsh-daemon", " -- doOnDispose ---  取消订阅 .... ")
                saveData()
            }
            .subscribe { aLong ->
                Log.d("wsh-daemon", "每 3 秒采集一次数据... count = $aLong")
                if (aLong ?: 0 > 0 && aLong ?: 0 % 18 == 0L) {
                    saveData()
                    Log.d("wsh-daemon", "   采集数据  saveCount = " + (aLong / 18 - 1))
                }
            }
    }

    private fun saveData() {
        val stamp = System.currentTimeMillis() / 1000
        if (Math.abs(mSaveDataStamp - stamp) >= 3) {
            // 处理业务逻辑
            Log.d("wsh-daemon", "保存数据到磁盘。")
        }
        mSaveDataStamp = stamp
    }
}
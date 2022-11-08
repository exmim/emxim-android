package com.skipper.exmim

import android.app.Application
import com.skipper.exmim_lib.*
import com.skipper.exmim_lib.pojo.ImMessage

class App : Application()  {

    companion object {
        private val IM_SERVER = "tcp://192.168.3.8:31912"
    }

    override fun onCreate() {
        super.onCreate()

        IMClient.init(this, MainActivity::class.java)
        IMClient.get(IM_SERVER).addCallback(object : IMCallback {
            override fun messageArrived(topic: String?, message: ImMessage) {

            }
        })
        val account = IMAccount(
            "test2",
            "test2"
        )
        IMClient.get(IM_SERVER).connect(account, ClientType.PHONE, object : IMConnectListener {
            override fun connected(clientId: String) {

            }

            override fun connectFailed(clientId: String, exception: Throwable?) {

            }
        })
        //需要在 Application 的 onCreate() 中调用一次 DaemonEnv.initialize()
        // 每一次创建进程的时候都需要对Daemon环境进行初始化，所以这里没有判断进程

//        val processName: String = ApkHelper.getProcessName(this.applicationContext)
//        if ("com.shihoo.daemonlibrary" == processName) {
//            // 主进程 进行一些其他的操作
//            Log.d("wsh-daemon", "启动主进程")
//        } else if ("com.shihoo.daemonlibrary:work" == processName) {
//            Log.d("wsh-daemon", "启动了工作进程")
//        } else if ("com.shihoo.daemonlibrary:watch" == processName) {
//            // 这里要设置下看护进程所启动的主进程信息
//            WatchProcessPrefHelper.mWorkServiceClass = MainWorkService::class.java
//            // 设置通知栏的UI
//            ForegroundNotificationUtils.setResId(R.mipmap.ic_launcher)
//            ForegroundNotificationUtils.setNotifyTitle("我是")
//            ForegroundNotificationUtils.setNotifyContent("渣渣辉")
//            Log.d("wsh-daemon", "启动了看门狗进程")
//        }
    }

    override fun onTerminate() {
        super.onTerminate()
        IMClient.release()
    }
}
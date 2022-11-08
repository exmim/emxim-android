package com.skipper.exmim

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.shihoo.daemon.DaemonEnv
import com.shihoo.daemon.IntentWrapper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        DaemonEnv.sendStartWorkBroadcast(this)
//        findViewById<TextView>(R.id.test).postDelayed({
//            DaemonEnv.startServiceSafely(
//                this@MainActivity,
//                MainWorkService::class.java
//            )
//        },1000)

    }
    override fun onBackPressed() {
        IntentWrapper.onBackPressed(this)
    }
}
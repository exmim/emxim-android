package com.skipper.exmim_lib.log

import android.util.Log

private const val IM_TAG = "EXMIM"

fun logd(message: String?) {
    Log.d(IM_TAG, message ?: "")
}

fun loge(message: String?) {
    Log.e(IM_TAG, message ?: "")
}
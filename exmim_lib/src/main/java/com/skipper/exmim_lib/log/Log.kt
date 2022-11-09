package com.skipper.exmim_lib.log

import timber.log.Timber

fun logd(message: String?) {
    Timber.d(message)
}

fun loge(message: String?) {
    Timber.e(message)
}
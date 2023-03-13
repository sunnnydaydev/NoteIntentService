package com.sunnyday.noteintentservice

import android.app.IntentService
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.util.Log

/**
 * Create by SunnyDay /03/13 17:40:59
 */
class DownLoadService : IntentService("FileDownLoadThread") {
    private val tag = "DownLoadService"
    override fun onHandleIntent(intent: Intent?) {
        Log.d(tag, "onHandleIntent Thread info： thread id -> ${Thread.currentThread().id} thread name -> ${Thread.currentThread().name}")

        Log.d(tag, "开始下载->")
        Log.d(tag, "下载ing...")
        SystemClock.sleep(3000)
        Log.d(tag, "下载完成")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "onCreate")
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        Log.d(tag, "onStart")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(tag, "onStartCommand：startId->$startId")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(tag, "onBind")
        return super.onBind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(tag, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy")
    }
}
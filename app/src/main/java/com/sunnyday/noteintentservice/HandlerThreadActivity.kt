package com.sunnyday.noteintentservice

import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_handler_thread.*
import kotlin.concurrent.thread

class HandlerThreadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handler_thread)
    }

    /**
     * test：mainThread send msg and subThread handle msg
     * */
    private fun test1() {
        thread {
            Looper.prepare()
            val handler = Handler() {
                when (it.what) {
                    0x11 -> {
                        Log.d("HandlerThreadActivity", "${Thread.currentThread()}收到:${it.obj}")
                    }
                }
                return@Handler true
            }
            runOnUiThread {
                Log.d("HandlerThreadActivity", "${Thread.currentThread()}UI线程发送一条消息")
                // ui 线程发送消息
                handler.sendMessage(Message().apply {
                    what = 0x11
                    obj = "UI线程发送一条消息"
                })
            }
            Looper.loop()
        }
    }

    /**
     * test：HandlerThread usage
     * */
    private fun test2() {
        // 1、创建对象，并开启线程
        val handlerThread = HandlerThread("HandlerThread")
        handlerThread.start()
        // 2、核心，使用两个参数的构造（这里的handleMessage 回调再子线程中的）
        val handler = Handler(handlerThread.looper) {
            // 处理消息
            return@Handler true
        }
        // 3、发送消息
        handler.sendEmptyMessage(0x11)
    }
}


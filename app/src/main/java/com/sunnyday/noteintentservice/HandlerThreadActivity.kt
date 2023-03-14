package com.sunnyday.noteintentservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import kotlinx.android.synthetic.main.activity_handler_thread.*
import kotlin.concurrent.thread

class HandlerThreadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handler_thread)
        // open a subThread
        thread {

        }
        mainToSub.setOnClickListener {

        }
    }

}
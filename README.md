# IntentService

# 简单使用

```kotlin
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
```

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.downLoad).setOnClickListener {
            startService(Intent(this,DownLoadService::class.java))
        }
    }
}
```

使用十分简单，

# 介绍

# 源码分析

# 替代方案

# 参考

[官方文档](https://developer.android.google.cn/reference/android/app/IntentService?hl=en)

[活儿好又不纠缠的IntentService](https://mp.weixin.qq.com/s?__biz=MzIxNjc0ODExMA==&mid=2247484238&idx=1&sn=27f732b316fe3886bb40d429ece96931&chksm=97851a6fa0f293799474fa6d45d7d3ae997c4a0657a438a7ea9fdb77d00aef4bb6986531fdb5&scene=38#wechat_redirect)

[Android IntentService详解（源码分析）](https://juejin.cn/post/6844904054854778894)
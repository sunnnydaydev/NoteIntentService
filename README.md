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

```kotlin
 //第一次开启 
 D/DownLoadService: onCreate
 D/DownLoadService: onStartCommand：startId->1
 D/DownLoadService: onStart
 D/DownLoadService: onHandleIntent Thread info： thread id -> 62 thread name -> IntentService[FileDownLoadThread]
 D/DownLoadService: 开始下载->
 D/DownLoadService: 下载ing...

 //第二次开启
 D/DownLoadService: onStartCommand：startId->2
 D/DownLoadService: onStart
 D/DownLoadService: 下载完成
 D/DownLoadService: onHandleIntent Thread info： thread id -> 62 thread name -> IntentService[FileDownLoadThread]
 D/DownLoadService: 开始下载->
 D/DownLoadService: 下载ing...
 D/DownLoadService: 下载完成
 D/DownLoadService: onDestroy
```

连续点击两次按钮，可以观察到log：

(1) IntentService的生命周期是这样的

onCreate -> onStartCommand -> onStart -> onHandleIntent -> onDestroy

(2) 系统默认帮我们开启一个子线程，我们可以在onHandleIntent中做一些耗时任务

(3) Service只会被创建一次，多次绑定onStartCommand回调多次

(4) 所有任务完成时onDestroy自动回调

# 介绍

- IntentService是Service的一个子类，内部维护了一个工作线程，他会把所有的任务都放到工作线程中处理。
- 多次开启IntentService时，每一次的开启的任务都会放到工作线程中处理，当所有的任务完成时他会自动调用stopSelf方法来结束Service
- onBind方法不会回调
- onStart方法内部进行了默认实现是IntentService的核心


# 源码分析

// todo 把HandlerThread 回顾下。 再分析IntentService。

# 替代方案

JobIntentService

# 参考

[官方文档](https://developer.android.google.cn/reference/android/app/IntentService?hl=en)

[活儿好又不纠缠的IntentService](https://mp.weixin.qq.com/s?__biz=MzIxNjc0ODExMA==&mid=2247484238&idx=1&sn=27f732b316fe3886bb40d429ece96931&chksm=97851a6fa0f293799474fa6d45d7d3ae997c4a0657a438a7ea9fdb77d00aef4bb6986531fdb5&scene=38#wechat_redirect)

[Android IntentService详解（源码分析）](https://juejin.cn/post/6844904054854778894)